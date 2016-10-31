package edu.bupt.ipoc.service;

public class PacketServiceChild extends PacketService {

	public PacketService ps_father = null;
	
	public PacketServiceChild(int _id, int _sourceVertex, int _sinkVertex, int _s_priority, int _static_bw,
			int _time_long) {
		super(_id, _sourceVertex, _sinkVertex, _s_priority, _static_bw, _time_long);
		// TODO Auto-generated constructor stub
	}
	
	public void setPsFather(PacketService _ps)
	{
		ps_father = _ps;
	}
	
	
	
	

}
