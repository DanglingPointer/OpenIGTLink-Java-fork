/*=========================================================================

  Program:   OpenIGTLink Library
  Module:    $HeadURL: http://osfe.org/OpenIGTLink/Source/org/medcare/igtl/messages/ImageMessage.java $
  Language:  java
  Date:      $Date: 2010-18-14 10:37:44 +0200 (ven., 13 nov. 2009) $
  Version:   $Revision: 0ab$

  Copyright (c) Absynt Technologies Ltd. All rights reserved.

  This software is distributed WITHOUT ANY WARRANTY; without even
  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
  PURPOSE.  See the above copyright notices for more information.

=========================================================================*/

package org.medcare.igtl.messages;

import org.medcare.igtl.util.BytesArray;
import org.medcare.igtl.util.Header;
//import com.neuronrobotics.sdk.common.ByteList;

/**
 *** This class create an Transform object from bytes received or help to generate
 * bytes to send from it
 * 
 * @author Andre Charles Legendre
 * 
 */
public class TransformMessage extends OpenIGTMessage {

        public static int IGTL_TRANSFORM_SIZE = 48;

        private double matrix[][] = new double[4][4];
        private double origin[] = new double[3]; // float 32bits
        // PX_pixels float 32bits origin[0] (matrix[0][3])
        // PY_pixels float 32bits origin[1] (matrix[1][3])
        // PZ_pixels float 32bits origin[2] (matrix[2][3])
        private double normals[][] = new double[3][3];
        private double t[] = new double[3]; // float 32bits
        // TX_pixels float 32bits t[0] (matrix[0][0])
        // TY_pixels float 32bits t[1] (matrix[1][0])
        // TZ_pixels float 32bits t[2] (matrix[2][0])
        private double s[] = new double[3]; // float 32bits
        // SX_pixels float 32bits s[0] (matrix[0][1])
        // SY_pixels float 32bits s[1] (matrix[1][1])
        // SZ_pixels float 32bits s[2] (matrix[2][1])
        private double n[] = new double[3]; // float 32bits
        // NX_pixels // float 32bits n[0] (matrix[0][2])
        // NY_pixels // float 32bits n[1] (matrix[1][2])
        // NZ_pixels // float 32bits n[2] (matrix[2][2])
        private byte[] transform_data;

        /**
         *** Constructor to be used to create message to send them with this
         * constructor you must use method SetImageHeader, then CreateBody and then
         * getBytes to send them
         *** 
         * @param deviceName
         *            Device Name
         **/
        public TransformMessage(String deviceName) {
                super(deviceName);
        }

        /**
         *** Constructor to be used to create message from received data
         * 
         * @param header
         * @param body
         * @throws Exception 
         */
        public TransformMessage(Header header, byte body[]) throws Exception {
                super(header, body);
                }

        public TransformMessage(String deviceName,double[] positionAray, double[][] rotationMatrix) {
        	super(deviceName);
        	SetTransformData(positionAray, rotationMatrix);
		}

		/**
         *** To create body from body array
         * 
         *** 
         * @return true if unpacking is ok
         */
        @Override
        public boolean UnpackBody() throws Exception {
        		//System.out.println("Unpacking Transform Body..");
                transform_data = new byte[IGTL_TRANSFORM_SIZE];
                System.arraycopy(body, 0, transform_data, 0, IGTL_TRANSFORM_SIZE);
                SetTransformData(transform_data);
                
               // System.out.println("Unpacking Transform Body OK!");
                return true;
        }

        /**
         *** To create body from image_header and image_data
         *  SetTransformData must have called first
         * 
         *** 
         * @return the bytes array containing the body
         */
        @Override
        public byte[] PackBody() {
                body = new byte[transform_data.length];
                System.arraycopy(transform_data, 0, body, 0, transform_data.length);
                header = new Header(VERSION, "TRANSFORM", deviceName, body);
                return getBytes();
        }

        /**
         *** To create transform_data from image characteristics and to get the byte array to send
         * @param origin
         * @param normals
         *** 
         * @return the bytes array created from the value
         */
        public byte[] SetTransformData(double origin[], double normals[][]) {
                bytesArray = new BytesArray();
                                           
                // the 3x3 rotation matrix
                SetNormals(normals);
                for (int i = 0; i < 3; i++)
                        for (int j = 0; j < 3; j++)
                                bytesArray.putDouble(normals[i][j], 4);
                SetMatrix(origin, normals);
                               
             // the position vector
                SetOrigin(origin);
                SetNormals(normals);
                bytesArray.putDouble(origin[0], 4);
                bytesArray.putDouble(origin[1], 4);
                bytesArray.putDouble(origin[2], 4);
                
                transform_data = bytesArray.getBytes();
                PackBody();
             //   System.out.println("++++++++++++++++++++++++++++++++++++++");
                return transform_data;
        }

        /**
         *** To extract image characteristics from transform_data byte array
         * @param transform_data
         */
        public void SetTransformData(byte transform_data[]) {
        		//System.out.println("Setting Transform Data: "+new ByteList(transform_data));
                this.transform_data = transform_data;
                bytesArray = new BytesArray();
                bytesArray.putBytes(transform_data);
                
                // the 3x3 rotation matrix
                normals = new double[3][3];
                for (int i = 0; i < 3; i++){
                        for (int j = 0; j < 3; j++){
                                normals[i][j] = bytesArray.getDouble(4); // float32
                             //   System.out.println("Normals ["+i+","+j+"] : "+normals[i][j]);
                        }      
                }
                // the position vector
                double[] o = new double[3];                
                for(int i=0;i<3;i++){
                	o[i] = bytesArray.getDouble(4); // float32
                //	System.out.println("Origin "+i+": "+o[i]);
                }
                
               // System.out.println("++++++++++++++++++++++++++++++++++++++");
                
                SetOrigin(o);
                SetNormals(normals);
                //SetMatrix(origin, normals);        
        }

        /**
         *** To get transform_data byte array
         *** 
         * @return the transform_data bytes array
         */
        public byte[] GetTransformData() {
                return this.transform_data;
        }

        /**
         *** To set Image origin
         * @param origin
         *** 
         */
        public void SetOrigin(double a,double b, double c) {
        	double [] o = {a,b,c};
        	SetOrigin(o);
        }
        /**
         *** To set Image origin
         * @param o
         *** 
         */
        public void SetOrigin(double o[]) {
        	if (o == null){
        		System.err.println("Trying to set origins to null");
        		return;
        	}
        	if(origin == null){
        		System.err.println("Origin was null, creating new one");
        		origin = new double[3];
        	}
        	//System.out.print("\nSetting Origins: [");
    		for(int i=0;i<origin.length;i++){
    			origin[i] = o[i];
    			//System.out.print(origin[i]+" ");
    		}
    		//System.out.print("]\n");
        }

        /**
         *** To get Image origin
         *** 
         * @return the origin bytes array
         */
        public double[] GetOrigin() {
        	if(this.origin == null){
        		System.err.println("Getting a null origin");
        		SetOrigin(new double[3]);
        	}
        	double[] o= new double[origin.length];
        	//System.out.print("\nGetting Origins: [");
    		for(int i=0;i<origin.length;i++){
    			o[i] = origin[i];
    			System.out.print(origin[i]+" ");
    		}
    		System.out.print("]\n");
            return o;
        }

        /**
         *** To set Image normals
         * @param normals
         *** 
         */
        void SetNormals(double normals[][]) {
        	for (int i = 0; i < 3; i++){
                for (int j = 0; j < 3; j++){
                        this.normals[i][j] = normals[i][j];
                      //  System.out.println("setting normals...");
                }      
        	}
        }

        /**
         *** To set Image normals
         * @param t array
         * @param s array
         * @param n array
         *** 
         */
        void SetNormals(double t[], double s[], double n[]) {
                normals = new double[3][3];
                this.t = t;
                this.s = s;
                this.n = n;
                normals[0][0] = t[0];
                normals[1][0] = t[1];
                normals[2][0] = t[2];
                normals[0][1] = s[0];
                normals[1][1] = s[1];
                normals[2][1] = s[2];
                normals[0][2] = n[0];
                normals[1][2] = n[1];
                normals[2][2] = n[2];
        }

        /**
         *** To get Image normals
         *** 
         * @return the normals matrix
         */
        public double[][] GetNormals() {
                return normals;
        }

        /**
         *** To set Image matrix
         * @param origin array
         * @param t array
         * @param s array
         * @param n array
         *** 
         */
        public void SetMatrix(double origin[],
                            double t[], double s[], double n[]) {
                SetOrigin(origin);
                this.t = t;
                this.s = s;
                this.n = n;
                matrix[0][0] = t[0];
                matrix[1][0] = t[1];
                matrix[2][0] = t[2];
                matrix[0][1] = s[0];
                matrix[1][1] = s[1];
                matrix[2][1] = s[2];
                matrix[0][2] = n[0];
                matrix[1][2] = n[1];
                matrix[2][2] = n[2];
                matrix[0][3] = origin[0];
                matrix[1][3] = origin[1];
                matrix[2][3] = origin[2];
        }

        /**
         *** To set Image matrix
         * @param origin array
         * @param normals matrix
         *** 
         */
        public void SetMatrix(double origin[], double normals[][]) {
        		SetOrigin(origin);
                this.normals = normals;
                matrix = new double[4][4];
                if (normals == null)
                {
                	System.err.println("null normal");
                }
                
                System.err.println("display normal matrix");
                printDoubleDataArray(normals);
                
                matrix[0][0] = 32;
                matrix[1][0] = normals[1][0];
                matrix[2][0] = normals[2][0];
                matrix[0][1] = normals[0][1];
                matrix[1][1] = normals[1][1];
                matrix[2][1] = normals[2][1];
                matrix[0][2] = normals[0][2];
                matrix[1][2] = normals[1][2];
                matrix[2][2] = normals[2][2];
                
                matrix[0][3] = origin[0];
                matrix[1][3] = origin[1];
                matrix[2][3] = origin[2];
                matrix[3][0] = 0.0;
                matrix[3][1] = 0.0;
                matrix[3][2] = 0.0;
                matrix[3][3] = 1.0;
                //SetMatrix(matrix);
        }

        /**
         *** To set Image matrix
         * @param matrix
         *** 
         */
        public void SetMatrix(double matrix[][]) {
                this.matrix = matrix;
                t = new double[3];
                s = new double[3];
                n = new double[3];

                t[0] = matrix[0][0];
                t[1] = matrix[1][0];
                t[2] = matrix[2][0];
                s[0] = matrix[1][1];
                s[1] = matrix[2][1];
                s[2] = matrix[3][1];
                n[0] = matrix[0][2];
                n[1] = matrix[1][2];
                n[2] = matrix[2][2];
                SetOrigin(matrix[0][3], matrix[1][3], matrix[2][3]);
        }

        /**
         *** To get Image matrix
         *** 
         * @return the image matrix
         */
        public double[][] GetMatrix() {
                return matrix;
        }

        /**
         *** To get transform String
         *** 
         * @return the transform String
         */
        @Override
        public String toString() {
                String transformString = "TRANSFORM Device Name           : " + getDeviceName();
                for (int i = 0; i <= 3; i++)
                        for (int j = 0; j <= 3; j++)
                                transformString = transformString.concat(Double.toString(matrix[i][j]));
                return transformString;
        }
        
        public void printDoubleDataArray(double[][] matrixArray) {
    		for (int i = 0; i < matrixArray.length; i++) {
    			for (int j = 0; j < matrixArray[0].length; j++) {
    				System.out.print(matrixArray[i][j] + " ");
    			}
    			System.out.println("\n");
    		}
    	}
}

