package spay.resources;

import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
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
        } catch (InvalidCardException e) {
            return Response.status(422) // UNPROCESSABLE_ENTITY
                    .entity("Invalid Card")
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
