package br.com.bejacob.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name:BeJacob}")
    private String senderName;

    private final RestClient restClient;

    public EmailService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com")
                .build();
    }

    @Async("emailSendExecutor")
    public void sendPasswordResetEmail(String toEmail, String userName, String resetLink) {
        try {
            String greeting = (userName != null && !userName.isBlank()) ? "Olá, " + userName : "Olá";

            String html = """
                    <div style="font-family: Arial, sans-serif; max-width: 580px; margin: 0 auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px;">
                        <h2 style="color: #222; text-align: center;">Recuperação de Senha</h2>
                        <p>%s,</p>
                        <p>Sua solicitação para redefinir a sua senha no BeJacob (expira em 15 minutos).</p>
                        <p>Clique no botão abaixo para escolher uma nova senha:</p>
                        <div style="text-align: center; margin: 32px 0;">
                            <a href="%s" style="background-color: #1a73e8; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;">
                                Redefinir Senha
                            </a>
                        </div>
                    </div>
                    """.formatted(greeting, resetLink);

            Map<String, Object> payload = Map.of(
                    "sender", Map.of(
                            "name", senderName,
                            "email", senderEmail
                    ),
                    "to", List.of(Map.of("email", toEmail)),
                    "subject", "BeJacob Redefinição da Senha",
                    "htmlContent", html
            );

            restClient.post()
                    .uri("/v3/smtp/email")
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("E-mail de recuperação de senha enviado com sucesso para: {}", toEmail);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de recuperação para {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
