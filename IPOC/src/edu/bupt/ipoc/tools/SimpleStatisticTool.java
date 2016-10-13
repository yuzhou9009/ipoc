package edu.bupt.ipoc.tools;

import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.PacketService;

import java.util.ArrayList;
import java.util.List;

public class SimpleStatisticTool implements Tool {

	public final static int DEBUG = 2;
	public final static int INFO = 1;
	public final static int SILENCE = 0;
	
	BasicController bc;
	public int msg_print = INFO;
	
	public List<PacketService> faultPacketServices = null;
	
	public SimpleStatisticTool(BasicController _bc)
	{
		bc = _bc;
		faultPacketServices = new ArrayList<PacketService>();
	}

	public void addFaultPacketService(PacketService _ps)
	{
		faultPacketServices.add(_ps);
		if(msg_print == INFO || msg_print == DEBUG)
		{
			System.out.println("This ps request cannot be carried successfully, id:"+_ps.id);
		}
			
	}
	
	public void cleanAllConfigurations()
	{
		faultPacketServices.clear();
		bc.clearAll();
	}
	
}
