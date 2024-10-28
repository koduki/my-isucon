package io.helidon.examples.quickstart.mp;

import javax.enterprise.context.RequestScoped;
import javax.persistence.*;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;

@Path("/payment")
@RequestScoped
public class PaymentService {

    @PersistenceContext(unitName = "paymentPU")
    private EntityManager em;

    @POST
    @Path("/register_user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response registerUser(User user) {
        try {
            if (em == null) {
                throw new IllegalStateException("EntityManager is not initialized");
            }
            em.persist(user);

            Card card = new Card();
            card.setUser(user);
            card.setCardNumber(generateCardNumber());
            card.setLimitAmount(generateLimitAmount());
            card.setUsedAmount(0);
            em.persist(card);

            user.getCards().add(card);
            return Response.ok(user).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Error registering user: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/add_card")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCard(@QueryParam("user_id") Long userId) {
        try {
            User user = em.find(User.class, userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("User not found"))
                        .build();
            }

            Card card = new Card();
            card.setUser(user);
            card.setCardNumber(generateCardNumber());
            card.setLimitAmount(generateLimitAmount());
            card.setUsedAmount(0);
            em.persist(card);

            user.getCards().add(card);
            return Response.ok(card).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Error adding card: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/purchase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response purchase(PurchaseRequest request) {
        try {
            Card card = em.createQuery("SELECT c FROM Card c WHERE c.cardNumber = :cardNumber", Card.class)
                    .setParameter("cardNumber", request.getCardNumber())
                    .getSingleResult();

            if (card == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Invalid card number"))
                        .build();
            }

            if (request.getAmount() > card.getLimitAmount() - card.getUsedAmount()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Limit exceeded"))
                        .build();
            }

            card.setUsedAmount(card.getUsedAmount() + request.getAmount());
            Transaction transaction = new Transaction();
            transaction.setCard(card);
            transaction.setItemName(request.getItemName());
            transaction.setAmount(request.getAmount());
            em.persist(transaction);

            return Response.ok(transaction).build();
        } catch (NoResultException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Card not found"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Error processing purchase: " + e.getMessage()))
                    .build();
        }
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private int generateLimitAmount() {
        return 1000000 + new Random().nextInt(9000000);
    }
}

