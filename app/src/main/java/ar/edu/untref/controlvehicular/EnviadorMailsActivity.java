package ar.edu.untref.controlvehicular;

import android.os.AsyncTask;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EnviadorMailsActivity extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {

        final String EMAIL_USERNAME = "tu_correo@gmail.com";
        final String EMAIL_PASSWORD = "tu_contraseña";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.mailinator.com");
        properties.put("mail.smtp.port", "25");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("otroCorreo@mailinator.com"));
            message.setSubject("Prueba de correo desde Mailinator");
            message.setText("Hola,\n\nEste es un correo de prueba enviado desde Mailinator.");

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
