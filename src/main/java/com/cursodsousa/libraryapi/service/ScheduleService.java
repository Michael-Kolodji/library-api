package com.cursodsousa.libraryapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cursodsousa.libraryapi.model.entity.Loan;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

	/*CRON
	 *{0} = segundo, {0} = minuto, {0} = hora, {1/1} = dias, {*} = mes, {?} = ano
	 * OBS. {1/1} todos os dias. {*} qlqr mes, {?} qlqr ano*/
	private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";
	
	private final LoanService loanService;
	private final EmailService emailService;
	
	@Value("${application.mail.lateloans.message}")
	private String message;
	
	@Scheduled(cron = CRON_LATE_LOANS)
	public void  sendMailToLateLoans() {
		List<Loan> allLateLoans = loanService.getAllLateLoans();
		List<String> mailsList = allLateLoans.stream()
						.map(loan -> loan.getCustomerEmail())
						.collect(Collectors.toList());
		
		emailService.sendMails(message, mailsList);
	}
}
