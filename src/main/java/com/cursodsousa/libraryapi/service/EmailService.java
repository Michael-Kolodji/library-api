package com.cursodsousa.libraryapi.service;

import java.util.List;

public interface EmailService {

	public void sendMails(String message, List<String> mailsList);

}
