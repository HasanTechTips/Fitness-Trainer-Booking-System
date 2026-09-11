package com.ftms.cancel.endpoint;

import com.ftms.cancel.auth.JwtVerifier;
import com.ftms.cancel.business.CancelManager;
import com.ftms.cancel.business.CancelResult;
import com.ftms.cancel.helper.BookingInfo;
import com.ftms.cancel.helper.BookingsXML;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/cancellations")
public class CancelEndpoint {
    private final CancelManager cancelManager = new CancelManager();

    @GET
    @Path("/member/{memberId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getBookings(@HeaderParam("Authorization") String authHeader,
            @PathParam("memberId") int memberId) {
        try {
            JwtVerifier.verifyAndGetClaims(authHeader);
            BookingsXML xml = cancelManager.getBookingsForMember(memberId);
            return Response.ok(xml).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(buildUnauthorized()).build();
        }
    }

    @POST
    @Path("/cancel")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    public Response cancelBooking(@HeaderParam("Authorization") String authHeader,
            @FormParam("memberId") int memberId,
            @FormParam("bookingId") int bookingId) {
        try {
            JwtVerifier.verifyAndGetClaims(authHeader);
            CancelResult result = cancelManager.cancelBooking(memberId, bookingId);
            return Response.ok(result).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(buildUnauthorized()).build();
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    public Response registerBookingCopy(@HeaderParam("Authorization") String authHeader,
            @FormParam("bookingId") int bookingId,
            @FormParam("memberId") int memberId,
            @FormParam("slotId") int slotId,
            @FormParam("trainerName") String trainerName,
            @FormParam("startTime") String startTime,
            @FormParam("endTime") String endTime) {
        try {
            JwtVerifier.verifyAndGetClaims(authHeader);

            BookingInfo booking = new BookingInfo();
            booking.setBookingID(bookingId);
            booking.setMemberID(memberId);
            booking.setSlotID(slotId);
            booking.setTrainerName(trainerName);
            booking.setStartTime(startTime);
            booking.setEndTime(endTime);
            booking.setBookingStatus("booked");

            CancelResult result = cancelManager.registerBookingCopy(booking);
            return Response.ok(result).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(buildUnauthorized()).build();
        }
    }

    private CancelResult buildUnauthorized() {
        CancelResult result = new CancelResult();
        result.setSuccess(false);
        result.setMessage("Unauthorized: invalid or missing token.");
        return result;
    }
}
