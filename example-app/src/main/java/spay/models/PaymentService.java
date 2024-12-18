/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spay.models;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author koduki
 */
@Dependent
public class PaymentService {

    @Inject
    TransactionDao transactionDao;

    @Inject
    CardDao cardDao;

    public PaymentTransaction purchase(PaymentTransaction request) throws LimitExceededException, InvalidCardException {
        for (Card card : cardDao.list()) {
            if (card.getCardNumber().equals(request.getCardNumber())) {
                verifyValid(card);
                verifyLimit(request.getAmount(), card);

                card.setUsedAmount(card.getUsedAmount() + request.getAmount());
                PaymentTransaction transaction = new PaymentTransaction();
                transaction.setCardNumber(card.getCardNumber());
                transaction.setItemName(request.getItemName());
                transaction.setAmount(request.getAmount());
                transactionDao.add(transaction);

                return transaction;
            }
        }
        return null;
    }

    public List<PaymentTransaction> history(String cardNumber) {
        return transactionDao.stream()
        .filter(t -> t.getCardNumber().equals(cardNumber))
        .collect(Collectors.toList());
    }

    private void verifyLimit(int requestAmount, Card card) throws LimitExceededException {
        if (card.getLimitAmount() < requestAmount + card.getUsedAmount()) {
            throw new LimitExceededException();
        }
    }

    private void verifyValid(Card card) throws InvalidCardException {
        if (!card.isEnable()) {
            throw new InvalidCardException();
        }
    }
}
