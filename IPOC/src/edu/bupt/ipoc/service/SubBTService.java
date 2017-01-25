package edu.bupt.ipoc.service;

public class SubBTService extends PacketService{
	

	public int static_bw = 0;
	public BandwidthTolerantPacketService father_btps = null;

	public SubBTService(int _id, int _sourceNode, int _destNode, int _priority, VirtualTransLink _carried_VTL, int _static_bw, BandwidthTolerantPacketService _father_btps) {
		super(_id, _sourceNode, _destNode, _priority);
		static_bw = _static_bw;
		father_btps = _father_btps;
		carriedVTL = _carried_VTL;
		// TODO Auto-generated constructor stub
	}

	public int bwCanShrinked() {
		// TODO Auto-generated method stub
		int tem = this.father_btps.maxBWCanBeShrinked();
		
		if(static_bw > tem)
			return tem;
		else 
			return static_bw;
	}

	public void shrinkItself(int request_bw) {
		// TODO Auto-generated method stub
		if(request_bw > static_bw)
			System.out.println("Bad input in SubBTService, check");
		else if(request_bw < static_bw)
		{
			static_bw -= request_bw;
			this.father_btps.updateCurrent_rate();
		}
		else
		{
			this.father_btps.removeSubBTService(this);
		}
	}

}
