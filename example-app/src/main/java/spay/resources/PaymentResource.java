package spay.resources;

import spay.models.PaymentTransaction;
import spay.models.Card;
import spay.models.User;
import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.inject.Inject;
import spay.models.*;

@Path("/payment")
@RequestScoped
public class PaymentResource {

    @Inject
    PaymentService service;

    @POST
    @Path("/register_user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response registerUser(User user) {
        service.registUser(user);
        return Response.ok(user).build();
    }

    @POST
    @Path("/add_card")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCard(@QueryParam("user_id") Long userId) {
        Card result = service.addCard(userId);
        return Response.ok(result).build();

    }

    @POST
    @Path("/purchase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response purchase(PaymentTransaction request) {
        try {
            PaymentTransaction transaction = service.purchase(request);
            return Response.ok(transaction).build();
        } catch (LimitExceededException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Limit exceeded")
                    .build();
        }
    }

}
