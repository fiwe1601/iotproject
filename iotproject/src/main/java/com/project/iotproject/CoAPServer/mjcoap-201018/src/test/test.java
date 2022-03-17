package test;

import org.zoolu.util.ByteUtils;
import org.zoolu.util.Flags;
import org.zoolu.util.LoggerLevel;
import org.zoolu.util.LoggerWriter;
import org.zoolu.util.SystemUtils;

import it.unipr.netsec.mjcoap.coap.client.CoapClient;
import it.unipr.netsec.mjcoap.coap.client.CoapResponseHandler;
import it.unipr.netsec.mjcoap.coap.message.CoapRequest;
import it.unipr.netsec.mjcoap.coap.message.CoapRequestMethod;
import it.unipr.netsec.mjcoap.coap.message.CoapResponse;
import it.unipr.netsec.mjcoap.coap.provider.CoapProvider;
import it.unipr.netsec.mjcoap.coap.provider.CoapURI;
import it.unipr.netsec.mjcoap.coap.server.CoapResource;

import java.net.SocketException;
import java.net.URISyntaxException;


/** Simple CoAP client.
 * It may send CoAP GET, PUT, and DELETE requests, or register for observing a remote resource.
 * <p>
 * It supports resource observation (RFC 7641) and blockwise transfer (RFC 7959). 
 */
public class test {
	
	/** Constructor is not available. */
	private test() {}


	/** The main method.
	 * @param args command-line arguments 
	 * @throws URISyntaxException 
	 * @throws SocketException */
	public static void main(String[] args) throws URISyntaxException, SocketException {
        // resource GET, PUT, POST, or DELETE
        CoapClient client1 = new CoapClient();
        CoapResponse resp = client1.request(CoapRequestMethod.GET, new CoapURI("coap://127.0.0.1/test"));
        if (resp!=null) System.out.println("Response: "+resp);
        else System.out.println("Request failure");	
	}
	

}
