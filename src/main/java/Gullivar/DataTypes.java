package Gullivar;

import java.nio.ByteBuffer;

public class DataTypes {

	public static byte[] toByteArray(double[] doubleArray){
	    int times = Double.SIZE / Byte.SIZE;
	    byte[] bytes = new byte[doubleArray.length * times];
	    for(int i=0;i<doubleArray.length;i++){
	        ByteBuffer.wrap(bytes, i*times, times).putDouble(doubleArray[i]);
	    }
	    return bytes;
	}

	public static double[] toDoubleArray(byte[] byteArray){
	    int times = Double.SIZE / Byte.SIZE;
	    double[] doubles = new double[byteArray.length / times];
	    for(int i=0;i<doubles.length;i++){
	        doubles[i] = ByteBuffer.wrap(byteArray, i*times, times).getDouble();
	    }
	    return doubles;
	}

	public static byte[] toByteArray(float[] floatArray){
	    int times = Float.SIZE / Byte.SIZE;
	    byte[] bytes = new byte[floatArray.length * times];
	    for(int i=0;i<floatArray.length;i++){
	        ByteBuffer.wrap(bytes, i*times, times).putFloat(floatArray[i]);
	    }
	    return bytes;
	}

	public static float[] toFloatArray(byte[] byteArray){
	    int times = Float.SIZE / Byte.SIZE;
	    float[] floats = new float[byteArray.length / times];
	    for(int i=0;i<floats.length;i++){
	    	floats[i] = ByteBuffer.wrap(byteArray, i*times, times).getFloat();
	    }
	    return floats;
	}

	public static byte[] toByteArray(int[] intArray){
	    int times = Integer.SIZE / Byte.SIZE;
	    byte[] bytes = new byte[intArray.length * times];
	    for(int i=0;i<intArray.length;i++){
	        ByteBuffer.wrap(bytes, i*times, times).putInt(intArray[i]);
	    }
	    return bytes;
	}

	public static int[] toIntArray(byte[] byteArray){
	    int times = Integer.SIZE / Byte.SIZE;
	    int[] ints = new int[byteArray.length / times];
	    for(int i=0;i<ints.length;i++){
	        ints[i] = ByteBuffer.wrap(byteArray, i*times, times).getInt();
	    }
	    return ints;
	}
}
