package edu.bupt.ipoc.service;

public class OTNService extends Service{
		
	public static final int OTN_1G = 1;
	public static final int ODU0 = 1;
	
	
	public static final int BW_1G = 1000;
	
	
	public int id;
	public int sourceVertex;
	public int destVertex;
	
	public VirtualTransLink vtlSupportted;
	public OpticalService osBelongTo;

	public OTNService(int _id, int _sourceVertex, int _destVertex) {
		
		id = _id;
		sourceVertex = _sourceVertex;
		destVertex = _destVertex;
	}

	public void setCarriedOpticalService(OpticalService _os) {
		
		this.osBelongTo = _os;
		
	}

	public void setVTLSupported(VirtualTransLink _vtl) {
		this.vtlSupportted = _vtl;
		
	}

	public int getBWCapacity() {
		
		return 1000;
	}

}
