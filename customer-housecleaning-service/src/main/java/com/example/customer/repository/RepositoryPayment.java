package com.example.customer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.customer.entity.Payment;

@Repository
public interface RepositoryPayment extends JpaRepository<Payment, Long> {

	@Query(nativeQuery = true, value = "SELECT ID,ID_CUSTOMER,CARD_TYPE,CARD_NUMBER FROM Payment WHERE ID_CUSTOMER =:idCustomer AND CARD_NUMBER =:cardNumber")
	Payment findPaymentByIdCustomerAndCardNumber(Long idCustomer, Long cardNumber);

	List<Payment> findPaymentByIdCustomer(Long id);

	void deletePaymentByIdCustomer(Long id);
	
}
