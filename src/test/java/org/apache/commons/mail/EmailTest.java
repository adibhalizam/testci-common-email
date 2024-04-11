package org.apache.commons.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EmailTest {
	Email emailObj = null;
	
	// SetUp method
	@Before
	public void setUp() {
		emailObj = new EmailConcrete();
	}
	
	// Teardown Method
	@After
    public void tearDown() {
        emailObj = null;
    }
	
	
	// Test Email addBcc(String... emails)
	@Test
	public void testAddBccIf() throws EmailException {
		String[] emails = {"test1@example", "test2@example"};
		emailObj.addBcc(emails);
		List<InternetAddress> bccList = emailObj.getBccAddresses();
        assertNotNull(bccList); // Ensure the list is not null
        assertEquals("Number of BCC addresses should be 2", 2, bccList.size());
        assertEquals("First BCC address should match", emails[0], bccList.get(0).toString());
        assertEquals("Second BCC address should match", emails[1], bccList.get(1).toString());
	}
		
	@Test
    public void testAddBccWithNullThrowsException() throws EmailException{
        String[] emails = null;
        try {
            emailObj.addBcc(emails); 
            fail("EmailException was expected to be thrown when adding null to Bcc");
        } catch (EmailException e) {
            assertEquals("Address List provided was invalid", e.getMessage());
        }
    }
	
	@Test
    public void testAddBccWithEmptyArray() throws EmailException{
        String[] emails = {};
        try {
            emailObj.addBcc(emails); 
            fail("EmailException was expected to be thrown when adding null to Bcc");
        } catch (EmailException e) {
            assertEquals("Address List provided was invalid", e.getMessage());
        }
    }
	
	// Test Email addCc(String email)
	@Test
	public void testaddCc() throws EmailException {
		emailObj.addCc("abc@def");
		List<InternetAddress> ccList = emailObj.getCcAddresses();
		assertEquals(1, ccList.size());
	}	
	
	//Test void addHeader(String name, String value)
	@Test
    public void testAddHeaderWithValidNameAndValue() {
        String name = "validHeader";
        String value = "validValue";

        emailObj.addHeader(name, value);
        String getValue = emailObj.headers.get(name);
        assertEquals(getValue, "validValue");
    }

    @Test
    public void testAddHeaderWithEmptyName() throws IllegalArgumentException{
        try {
        	emailObj.addHeader("", "validValue");
        } catch(IllegalArgumentException iae) {
        	assertEquals(iae.getMessage(), "name can not be null or empty");
        }
    }

    @Test
    public void testAddHeaderWithNullName() throws IllegalArgumentException{
    	try {
        	emailObj.addHeader(null, "validValue");
        } catch(IllegalArgumentException iae) {
        	assertEquals(iae.getMessage(), "name can not be null or empty");
        }
    }

    @Test
    public void testAddHeaderWithEmptyValue() throws IllegalArgumentException{
    	try {
        	emailObj.addHeader("validHeader", "");
        } catch(IllegalArgumentException iae) {
        	assertEquals(iae.getMessage(), "value can not be null or empty");
        }
    }

    @Test
    public void testAddHeaderWithNullValue() throws IllegalArgumentException {
    	try {
        	emailObj.addHeader("validHeader", null);
        } catch(IllegalArgumentException iae) {
        	assertEquals(iae.getMessage(), "value can not be null or empty");
        }
    }
    
    //Test Email addReplyTo(String email, String name)
    @Test
	public void testaddReplyTo() throws EmailException {
		emailObj.addReplyTo("abc@def", "Name");
		List<InternetAddress> replyList = emailObj.getReplyToAddresses();
		assertEquals(1, replyList.size());
		assertEquals(replyList.get(0).getAddress(), "abc@def");
		assertEquals(replyList.get(0).getPersonal(), "Name");
	}
    
    //Test buildMimeMessage()
    @Test(expected = RuntimeException.class)
    public void testBuildMimeMessageNullCheck() throws Exception {
    	try {
    		emailObj.setHostName("localhost");
    		emailObj.setSmtpPort(1234);
    		emailObj.setFrom("a@b.com");
    		emailObj.addTo("c@d.com");
    		emailObj.setSubject("test mail");
    		emailObj.setCharset("ISO-8859-1");
    		emailObj.setContent("test content", "text/plain");
    		emailObj.buildMimeMessage();
    		emailObj.buildMimeMessage();
    	} catch(RuntimeException re) {
    		String message = "The MimeMessage is already built.";
    		assertEquals(message, re.getMessage());
    		throw re;
    	}
    }
    
    @Test
    public void testBuildMimeMessageWithCharset() throws EmailException, MessagingException {
        emailObj.setHostName("localhost");
        emailObj.setSmtpPort(1234);
        emailObj.setFrom("a@b.com");
        emailObj.addTo("c@d.com");
        emailObj.setSubject("test mail");
        emailObj.setCharset("ISO-8859-1");
        emailObj.setContent("test content", "text/plain");
        emailObj.buildMimeMessage();
        
        assertEquals(emailObj.message.getSubject(), "test mail");
    }
    
    @Test
    public void testBuildMimeMessageWithoutCharset() throws EmailException, MessagingException {
        emailObj.setHostName("localhost");
        emailObj.setSmtpPort(1234);
        emailObj.setFrom("a@b.com");
        emailObj.addTo("c@d.com");
        emailObj.setSubject("test mail");
        emailObj.setContent("test content", "text/plain");
        emailObj.buildMimeMessage();
        
        assertEquals(emailObj.message.getSubject(), "test mail");
    }
    
    @Test
    public void testBuildMimeMessageEmptyContentAndContentType() throws EmailException, MessagingException {
        emailObj.setHostName("localhost");
        emailObj.setSmtpPort(1234);
        emailObj.setFrom("a@b.com");
        emailObj.addTo("c@d.com");
        emailObj.setSubject("test mail");
        emailObj.setCharset("ISO-8859-1");
        emailObj.buildMimeMessage();
        
        assertEquals(emailObj.content, emailObj.emailBody);
    }
    
    
    @Test
    public void testBuildMimeMessageWithoutFromAddress() throws EmailException, MessagingException {
        try{
        	emailObj.setHostName("localhost");
        	emailObj.setSmtpPort(1234);
            emailObj.addTo("c@d.com");
            emailObj.setSubject("test mail");
            emailObj.setCharset("ISO-8859-1");
            emailObj.setContent("test content", "text/plain");
            emailObj.buildMimeMessage();
        }catch(EmailException ee) {
        	assertEquals(ee.getMessage(), "From address required");
        }
    }
    
    @Test
    public void testBuildMimeMessageToListBccListCCListZero() throws EmailException, MessagingException {
    	try {
	    	emailObj.setHostName("localhost");
	        emailObj.setSmtpPort(1234);
	        emailObj.setFrom("a@b.com");
	        emailObj.setSubject("test mail");
	        emailObj.setCharset("ISO-8859-1");
	        emailObj.setContent("test content", "text/plain");
	        emailObj.buildMimeMessage();
    	} catch(EmailException ee) {
    		assertEquals(emailObj.toList.size() + emailObj.bccList.size() + emailObj.ccList.size(), 0);
    		assertEquals(ee.getMessage(), "At least one receiver address required");
    	}  
    }
    
    @Test
    public void testBuildMimeMessageWithToList() throws EmailException, MessagingException {
    	emailObj.setHostName("localhost");
        emailObj.setSmtpPort(1234);
        emailObj.setFrom("a@b.com");
        emailObj.addTo("c@d.com");
        emailObj.addCc("cc@email.com");
        emailObj.setSubject("test mail");
        emailObj.setCharset("ISO-8859-1");
        emailObj.setContent("test content", "text/plain");
        emailObj.buildMimeMessage();
        
        List<InternetAddress> toList = emailObj.getToAddresses();
		assertEquals(1, toList.size());
		assertEquals(toList.get(0).getAddress(), "c@d.com");
    }
    
    @Test
    public void testBuildMimeMessageWithCCList() throws EmailException, MessagingException {
        emailObj.setHostName("localhost");
        emailObj.setSmtpPort(1234);
        emailObj.setFrom("a@b.com");
        emailObj.addTo("c@d.com");
        emailObj.addCc("cc@email.com");
        emailObj.setSubject("test mail");
        emailObj.setCharset("ISO-8859-1");
        emailObj.setContent("test content", "text/plain");
        emailObj.buildMimeMessage();
        
        List<InternetAddress> ccList = emailObj.getCcAddresses();
		assertEquals(1, ccList.size());
		assertEquals(ccList.get(0).getAddress(), "cc@email.com");
    }
    
    @Test
    public void testBuildMimeMessageWithBccList() throws EmailException, MessagingException {
    	emailObj.setHostName("localhost");
        emailObj.setSmtpPort(1234);
        emailObj.setFrom("a@b.com");
        emailObj.addTo("c@d.com");
        emailObj.addBcc("bcc@email.com");
        emailObj.setSubject("test mail");
        emailObj.setCharset("ISO-8859-1");
        emailObj.setContent("test content", "text/plain");
        emailObj.buildMimeMessage();
        
        List<InternetAddress> bccList = emailObj.getBccAddresses();
		assertEquals(1, bccList.size());
		assertEquals(bccList.get(0).getAddress(), "bcc@email.com");
    }
    
    @Test
    public void testBuildMimeMessageWithReplyList() throws EmailException, MessagingException {
    	emailObj.setHostName("localhost");
        emailObj.setSmtpPort(1234);
        emailObj.setFrom("a@b.com");
        emailObj.addTo("c@d.com");
        emailObj.addReplyTo("reply@email.com");
        emailObj.setSubject("test mail");
        emailObj.setCharset("ISO-8859-1");
        emailObj.setContent("test content", "text/plain");
        emailObj.buildMimeMessage();
        
        List<InternetAddress> replyList = emailObj.getReplyToAddresses();
        assertEquals(1, replyList.size());
        assertEquals(replyList.get(0).getAddress(), "reply@email.com");
    }
    
    @Test
    public void testBuildMimeMessageWithHeaders() throws EmailException, MessagingException {
    	emailObj.setHostName("localhost");
        emailObj.setSmtpPort(1234);
        emailObj.setFrom("a@b.com");
        emailObj.addTo("c@d.com");
        emailObj.addHeader("Title", "Header");
        emailObj.setSubject("test mail");
        emailObj.setCharset("ISO-8859-1");
        emailObj.setContent("test content", "text/plain");
        emailObj.buildMimeMessage();
        
        String header = emailObj.headers.get("Title");
        assertEquals(header, "Header");
    }
    
    //Test String getHostName()
    @Test
    public void testGetHostName() {
    	Properties props = new Properties();
    	Session sessionObj = Session.getInstance(props);
    	emailObj.setMailSession(sessionObj);
    	Properties sessionProps = sessionObj.getProperties();
    	String host = (String) sessionProps.get("mail.smtp.host");
    	
    	String getHost = emailObj.getHostName();
    	assertEquals(host, getHost);
    }
	
    @Test
    public void testGetHostNameNullSession() throws EmailException {
    	emailObj.setHostName("localhost");
    	String getHost = emailObj.getHostName();
    	assertEquals(getHost, "localhost");
    }
    
    @Test
    public void testGetHostNameNullSessionEmptyHostname() {
    	String getHost = emailObj.getHostName();
    	assertEquals(getHost, null);
    }
    
    // Test Session getMailSession()
    @Test
    public void testgetMailSessionWithSessionObject() throws EmailException {
    	Properties props = new Properties();
    	Session sessionObj = Session.getInstance(props);
    	emailObj.setMailSession(sessionObj);
    	Session gsession = emailObj.getMailSession();
    	assertEquals(sessionObj, gsession);
    }
    
    @Test
    public void testgetMailSessionWithNullSession() throws EmailException {
    	emailObj.setHostName("localhost");
    	emailObj.setSSLOnConnect(true); 
    	Session session = emailObj.getMailSession();
    	Properties getProp = session.getProperties();
    	String protocol = (String) getProp.get("mail.transport.protocol");
    	assertEquals(protocol, EmailConstants.SMTP);
    }
    
    @Test
    public void testGetMailSessionWithEmptyHostname() throws EmailException {
    	try {
    		Session session = emailObj.getMailSession();
    	} catch(EmailException ee) {
    		assertEquals(ee.getMessage(), "Cannot find valid hostname for mail session");
    	}
    }
    
    @Test
    public void testGetMailSessionWithAuthenticator() throws EmailException {
    	emailObj.setHostName("localhost");
    	Authenticator authenticator = new DefaultAuthenticator("username", "password");
    	emailObj.setAuthenticator(authenticator);
    	Session session = emailObj.getMailSession();
    	Properties getProp = session.getProperties();
    	String getAuthentication = getProp.getProperty("mail.smtp.auth");
    	assertEquals("true", getAuthentication);
    }
    
    @Test
    public void testGetMailSessionWithSSLOnConnectAndSSLCheckServerIdentity() throws EmailException {
    	emailObj.setHostName("localhost");
    	emailObj.setSSLOnConnect(true); 
    	emailObj.setSSLCheckServerIdentity(true);
    	Session session = emailObj.getMailSession();
    	Properties getProp = session.getProperties();
    	String SSLCheckServerIdentity = getProp.getProperty("mail.smtp.ssl.checkserveridentity");
    	assertEquals(SSLCheckServerIdentity, "true");
    }
     
   @Test
   public void testGetMailSessionWithBounceAddress() throws EmailException {
	   emailObj.setHostName("localhost");
	   emailObj.setBounceAddress("bounce@email.com");
	   Session session = emailObj.getMailSession();
	   Properties getProp = session.getProperties();
	   String getFrom = getProp.getProperty("mail.smtp.from");
	   assertEquals(getFrom, "bounce@email.com");
   }
        
    // Test Date getSentDate()
    @Test
    public void testGetSentDate() {
    	Date testDate = new Date(2024, 03, 26);
    	emailObj.setSentDate(testDate);
    	Date getDate = emailObj.getSentDate();
    	assertEquals(testDate, getDate);
    }
    
    // Test int getSocketConnectionTimeout()
    @Test
    public void testGetSocketConnectionTimeout() {
    	int socketConnectionTimeout = emailObj.getSocketConnectionTimeout();
    	assertEquals(EmailConstants.SOCKET_TIMEOUT_MS, socketConnectionTimeout);
    }
    
    
    // Test Email setFrom(String email)
    @Test
    public void testSetFrom() throws EmailException {
    	String emailFrom = "from@email.com";
    	emailObj.setFrom(emailFrom);
    	InternetAddress getFrom = emailObj.getFromAddress();
    	assertEquals(getFrom.getAddress(), emailFrom);
    }
}
