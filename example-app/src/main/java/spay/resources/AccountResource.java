package spay.resources;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.inject.Inject;
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
        Card res = service.addCard(userId);
        return Response.ok(res).build();
    }
}
