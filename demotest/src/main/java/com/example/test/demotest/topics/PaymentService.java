package com.example.test.demotest.topics;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.paypal.api.payments.Address;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.ShippingAddress;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Service
public class PaymentService {

	String clientId = "AQQC4QL4lBkzIcL0A-LHfNQfg1bnZ-nJuyh73XEgVOumFx8mIZ7kv7seOVJOq5X_gOwWcwt3bd_YaZ94";

	String clientSecret = "EFZcYZvjek76kgL-mKidakWT6hyF4XPuMiHRAaLSkbLPGJF6v7M0LjV8cyzG1xbfpXyvaddZeOSUbSws";

	public String createPayment(PaymentData detail) throws IOException {
				
		Amount amount = new Amount();
		amount.setCurrency("USD");
		amount.setTotal(detail.getOrdertotal());
		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);

		PayerInfo payerInfo = new PayerInfo();
		payerInfo.setFirstName(detail.getFirstname());
		payerInfo.setLastName(detail.getLastname());
		Address billingAddress = new Address();
		billingAddress.setLine1(detail.getAddress1());
		billingAddress.setLine2(detail.getAddress2());
		billingAddress.setCity(detail.getCity());
		billingAddress.setState(detail.getState());
		billingAddress.setPostalCode(detail.getZipcode());
		billingAddress.setCountryCode("US");
		payerInfo.setBillingAddress(billingAddress);
		ShippingAddress shippingAddress = new ShippingAddress();
		shippingAddress.setRecipientName(detail.getFirstname()+" "+detail.getLastname());
		shippingAddress.setLine1(detail.getAddress1());
		shippingAddress.setLine2(detail.getAddress2());
		shippingAddress.setCity(detail.getCity());
		shippingAddress.setState(detail.getState());
		shippingAddress.setPostalCode(detail.getZipcode());
		shippingAddress.setCountryCode("US");
		payerInfo.setShippingAddress(shippingAddress);
		payer.setPayerInfo(payerInfo);
		
		String workingDir = System.getProperty("user.dir");	
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl("http://localhost:8080/cancel");
		redirectUrls.setReturnUrl("http://localhost:8080/complete/payment");
		payment.setRedirectUrls(redirectUrls);
		Payment createdPayment;
		try {
			String redirectUrl = "";
			APIContext context = new APIContext(clientId, clientSecret, "sandbox");
			context.addHTTPHeader("Access-Control-Allow-Origin", "*");

			createdPayment = payment.create(context);
			if (createdPayment != null) {
				List<Links> links = createdPayment.getLinks();
				for (Links link : links) {
					if (link.getRel().equals("approval_url")) {
						redirectUrl = link.getHref();
						break;
					}
				}

				return redirectUrl;

			}
		} catch (PayPalRESTException e) {
			System.out.println("Error happened during payment creation!");
		}
		return "/";
	}

	public Payment completePayment(String paymentId, String PayerID) {
		System.out.println("it came to complete payment");
		Map<String, Object> response = new HashMap<String, Object>();
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl("http://localhost:8080/cancel");

		Payment payment = new Payment();
		payment.setRedirectUrls(redirectUrls);
		payment.setId(paymentId);
		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId(PayerID);
		Payment createdPayment = null;
		try {

			APIContext context = new APIContext(clientId, clientSecret, "sandbox");
			context.addHTTPHeader("Access-Control-Allow-Origin", "/*");
			createdPayment = payment.execute(context, paymentExecution);
			if (createdPayment != null) {
				response.put("status", "success");
				response.put("payment", createdPayment);
				System.out.println(createdPayment);

			}
		} catch (PayPalRESTException e) {
			System.err.println(e.getDetails());
		}
		return createdPayment;
	}

}
