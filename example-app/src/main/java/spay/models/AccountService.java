/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spay.models;

import java.util.Random;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author koduki
 */
@Dependent
public class AccountService {

    @Inject
    private CardDao cardDao;
    @Inject
    private UserDao userDao;

    public User registUser(User user) {
        user.setCustomerNumber(generateCustomerNumber());
        userDao.add(user);
        issueCard(user.getCustomerNumber());

        return user;
    }

    public User getUser(long customerNumber) {
        List<User> users = userDao.stream()
                .filter(u -> u.getCustomerNumber().equals(customerNumber))
                .collect(Collectors.toList());

        for (User u : users) {
            if (u.getCustomerNumber().equals(customerNumber)) {
                return u;
            }
        }
        return null;
    }

    public Card issueCard(long customerNumber) {
        User user = getUser(customerNumber);

        Card card = new Card();
        card.setCardNumber(generateCardNumber());
        card.setLimitAmount(generateLimitAmount());
        card.setUsedAmount(0);

        int cardType = new java.util.Random().nextInt(2);
        card.setCardType(cardType);
        String imagePath = cardType == 0 ? "images/card_normal.png"
                        : cardType == 1 ? "images/card_gold.png"
                        : null;
        byte[] cardFace = readImage(imagePath);
        card.setCardFace(cardFace);

        cardDao.add(card);

        user.getCards().add(card);
        userDao.add(user);

        return card;
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

    private long generateCustomerNumber() {
        Random random = new Random(System.nanoTime());
        return 1000000000L + random.nextInt(900000000);
    }

    private byte[] readImage(String path) {
        System.out.println(getClass().getClassLoader().getResource("."));

        System.out.println(getClass().getClassLoader().getResource(path));
        try (InputStream is = ClassLoader.getSystemResourceAsStream(path)) {
            if (is != null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                return buffer.toByteArray();
            } else {
                throw new FileNotFoundException("Not found: " + path);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}