package com.example.test.demotest.topics;

import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.paypal.api.payments.Payment;

@RestController
public class PaymentController {

	@Autowired
	private PaymentService paymentservice;

	@RequestMapping(value = "/create/payment")
	public ModelAndView makePayment(@ModelAttribute PaymentData detail) throws IOException {
		String url = paymentservice.createPayment(detail);
		return new ModelAndView("redirect:" + url);

	}

	@RequestMapping(value = "/complete/payment")
	public String completePayment(@RequestParam Map<String, String> requestParams) {

		String paymentId = requestParams.get("paymentId");
		String PayerID = requestParams.get("PayerID");
		Payment p = paymentservice.completePayment(paymentId, PayerID);

		return "<html><head></head>" + "<body>" + "<h3>" + "Your Payment is successfull with payment id=" + p.getId()
				+ ", Intent= " + p.getIntent() + "</h3>" + "</br>"

				+ "<h3>" + "state=" + p.getState() + ", create time: " + p.getCreateTime() + "</h3>" + "</br>"

				+ "</body></html>";
	}

}
