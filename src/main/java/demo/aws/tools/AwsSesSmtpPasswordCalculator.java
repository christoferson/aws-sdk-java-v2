package demo.aws.tools;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * https://docs.aws.amazon.com/ses/latest/dg/smtp-credentials.html#smtp-credentials-console
 * 
 * AwsSesSmtpPasswordCalculator.calculate("0LlWNNpMyVkgMXXXXXX", "eu-west-1");
 *
 */
public class AwsSesSmtpPasswordCalculator {

	static final String DATE = "11111111";
	static final String SERVICE = "ses";
	static final String TERMINAL = "aws4_request";
	static final String MESSAGE = "SendRawEmail";
	static final byte VERSION4 = 0x04;
	
	public static String calculate(String key, String region) {
		
		Objects.requireNonNull(key);
		Objects.requireNonNull(region);

		try {

			byte[] kDate = sign(DATE, ("AWS4" + key).getBytes());
			byte[] kRegion = sign(region, kDate);
			byte[] kService = sign(SERVICE, kRegion);
			byte[] kTerminal = sign(TERMINAL, kService);
			byte[] kMessage = sign(MESSAGE, kTerminal);

			// signatureAndVersion = Concatenate(version, kMessage);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(new byte[] { VERSION4 });
			outputStream.write(kMessage);
			byte[] rawSignatureWithVersion = outputStream.toByteArray();

			// smtpPassword = Base64(signatureAndVersion);
			return Base64.getEncoder().encodeToString(rawSignatureWithVersion);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

	private static byte[] sign(String data, final byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(secretKey);
		return mac.doFinal(data.getBytes());
	}
	  
}
