package com.ftms.booking.endpoint;

import com.ftms.booking.auth.JwtVerifier;
import com.ftms.booking.business.BookingManager;
import com.ftms.booking.business.BookingResult;
import com.ftms.booking.helper.BookingsXML;
import com.ftms.booking.helper.SlotsXML;
import com.ftms.booking.helper.TrainersXML;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/bookings")
public class BookingEndpoint {
    private final BookingManager bookingManager = new BookingManager();

    @GET
    @Path("/trainers")
    @Produces(MediaType.APPLICATION_XML)
    public TrainersXML getTrainers() throws Exception {
        return bookingManager.getAllTrainers();
    }

    @GET
    @Path("/slots")
    @Produces(MediaType.APPLICATION_XML)
    public SlotsXML getAvailableSlots(@QueryParam("trainerId") Integer trainerId) throws Exception {
        return bookingManager.getAvailableSlots(trainerId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    public Response createBooking(@HeaderParam("Authorization") String authHeader,
            @FormParam("memberId") int memberId,
            @FormParam("slotId") int slotId) {
        try {
            JwtVerifier.verifyAndGetClaims(authHeader);
            BookingResult result = bookingManager.bookTrainer(memberId, slotId);
            return Response.ok(result).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(buildUnauthorizedResult()).build();
        }
    }

    @POST
    @Path("/release")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    public Response releaseBooking(@HeaderParam("Authorization") String authHeader,
            @FormParam("bookingId") int bookingId) {
        try {
            JwtVerifier.verifyAndGetClaims(authHeader);
            BookingResult result = bookingManager.releaseBooking(bookingId);
            return Response.ok(result).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(buildUnauthorizedResult()).build();
        }
    }

    @GET
    @Path("/member/{memberId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getBookingsForMember(@HeaderParam("Authorization") String authHeader,
            @PathParam("memberId") int memberId) {
        try {
            JwtVerifier.verifyAndGetClaims(authHeader);
            BookingsXML xml = bookingManager.getBookingsForMember(memberId);
            return Response.ok(xml).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(buildUnauthorizedResult()).build();
        }
    }

    private BookingResult buildUnauthorizedResult() {
        BookingResult result = new BookingResult();
        result.setSuccess(false);
        result.setMessage("Unauthorized: invalid or missing token.");
        return result;
    }
}
