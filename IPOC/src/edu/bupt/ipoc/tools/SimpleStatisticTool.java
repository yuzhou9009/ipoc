package edu.bupt.ipoc.tools;

import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.PacketService;

import java.text.DecimalFormat;
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
	
	public void getPakcetServiceLatencyStatistics(List<PacketService> psl)
	{
		DecimalFormat decimalFormat=new DecimalFormat("0.000000");
		double latency_1 = 0.0;
		double latency_2 = 0.0;
		int bw_1 = 0;
		int bw_2 = 0;
		for(PacketService _pp : psl)
		{
			if(_pp.carriedVTL!=null)
			{
				if(_pp.priority == PacketService.PRIORITY_HIGH)
				{
					if(_pp.carriedVTL!=null)
						latency_1 += (_pp.getCurrentBw()*_pp.carriedVTL.getPathLong());
						bw_1 += _pp.getCurrentBw();
				}
				else if(_pp.priority == PacketService.PRIORITY_MID)
				{
					latency_2 += (_pp.getCurrentBw()*_pp.carriedVTL.getPathLong());
					bw_2 += _pp.getCurrentBw();
				}
			}			
		}
		System.out.println("\tlcy1 is\t"+decimalFormat.format(latency_1/bw_1/(3*100000))+"\tlcy2 is\t"+decimalFormat.format(latency_2/bw_2/(3*100000)));		
	}	
}
