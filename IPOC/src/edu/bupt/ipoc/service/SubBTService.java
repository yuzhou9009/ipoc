package edu.bupt.ipoc.service;

public class SubBTService extends PacketService implements Comparable<PacketService>{
	

	public int static_bw = 0;
	public BandwidthTolerantPacketService father_btps = null;

	public SubBTService(int _id, int _sourceNode, int _destNode, int _priority, VirtualTransLink _carried_VTL, int _static_bw, BandwidthTolerantPacketService _father_btps) {
		super(_id, _sourceNode, _destNode, _priority);
		static_bw = _static_bw;
		father_btps = _father_btps;
		carriedVTL = _carried_VTL;
	}

	public int bwCanShrinked() {
		int tem = this.father_btps.maxBWCanBeShrinked();
		
		if(static_bw > tem)
			return tem;
		else 
			return static_bw;
	}
	
	public int getCurrentOccupiedBw()
	{
		return this.static_bw;
	}

	public boolean shrinkItself(int request_bw) {
		if(request_bw > static_bw)
			System.out.println("Bad input in SubBTService, check");
		else if(request_bw < static_bw)
		{
			static_bw -= request_bw;
			this.father_btps.updateCurrent_rate();
		}
		else
			return true;
		return false;
	}

	@Override
	public int compareTo(PacketService arg0) {
		if(arg0 instanceof SubBTService)
			return this.father_btps.compareTo(((SubBTService)arg0).father_btps);
		else
			return super.compareTo(arg0);
	}

	public int bwCanExpended() {
		int tem = this.father_btps.maxBWCanBeExpended();
		
		return tem;
	}

	public void expendItself(int tem) {
		this.static_bw += tem;
		
	}
	
	public String toString(){
		
		String describtion = new String();
		
		describtion += "id" + this.id;
		describtion += "\t static bw" + this.static_bw;
		return describtion;
	}
}
