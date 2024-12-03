package spay.resources;

import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import spay.models.*;

@Path("/account")
@RequestScoped
public class AccountResource {
    @Inject
    AccountService service;

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response registerUser(User user) {
        User res = service.registUser(user);
        return Response.ok(res).build();
    }

    @GET
    @Path("/{user_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getUser(@PathParam("user_id") Long userId) {
        User res = service.getUser(userId);
        return Response.ok(res).build();
    }

    @POST
    @Path("/{user_id}/card")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCard(@PathParam("user_id") Long userId) {
        Card res = service.issueCard(userId);
        return Response.ok(res).build();
    }

    @DELETE
    @Path("/cards/{card_number}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response disableCard(@PathParam("card_number") String cardNumber) {
        service.disableCard(cardNumber);
        return Response.ok(cardNumber).build();
    }
}
