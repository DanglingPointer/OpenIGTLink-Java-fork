package sample;
import com.neuronrobotics.sdk.common.Log;
import org.medcare.igtl.messages.ImageMessage;
import org.medcare.igtl.network.GenericIGTLinkClient;
import org.medcare.igtl.network.IOpenIgtPacketListener;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class ClientSample implements IOpenIgtPacketListener {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenericIGTLinkClient client;
		try {
			Log.debug("Starting client");
			client = new GenericIGTLinkClient ("127.0.0.1",18944);
			client.addIOpenIgtOnPacket(new ClientSample());	
			
			for(int i=0;i<5;i++){
				Thread.sleep(1000);
			}
			client.stopClient();
			Log.debug("Client disconnected");
			System.exit(0);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onRxTransform(String name, TransformNR t) {
		//Log.debug("Received Transform: "+t);  
		if(name.equals("RegistrationTransform") || name.equals("CALIBRATION")){
			System.err.println("Received Registration Transform");
			Log.debug("Setting fiducial registration matrix: "+t); 
			return;
		}else if(name.equals("TARGET")){
			System.err.println("Received RAS Transform: TARGET");
			Log.debug("Setting task space pose: "+t); 
			
		}else if(name.equals("myTransform")){
			System.err.println("Received Transformation Matrix: myTransform");
			Log.debug("Setting task space pose: "+t); 
			
		}else{
			System.err.println("Received unidentified transform matrix");
			Log.debug("Setting task space pose: "+t); 
		}
	}

	@Override
	public TransformNR getTxTransform(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onRxString(String name, String body) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String onTxString(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onRxDataArray(String name, Matrix data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double[] onTxDataArray(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onRxImage(String name, ImageMessage image) {
		// TODO Auto-generated method stub
		
	}
}
