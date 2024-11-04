package spay.resources;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.inject.Inject;
import java.util.List;
import spay.models.*;

@Path("/payment")
@RequestScoped
public class PaymentResource {

    @Inject
    PaymentService service;

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
            return Response.status(422) // UNPROCESSABLE_ENTITY
                    .entity("Limit exceeded")
                    .build();
        }
    }

    @GET
    @Path("/history/{card_number}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response history(@PathParam("card_number") String cardNumber) {
        List<PaymentTransaction> transactions = service.history(cardNumber);
        return Response.ok(transactions).build();
    }

}
