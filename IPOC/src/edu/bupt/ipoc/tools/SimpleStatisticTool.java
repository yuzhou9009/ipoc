package edu.bupt.ipoc.tools;

import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.BestEffortPacketService;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.VirtualTransLink;

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
			System.out.println("This ps request cannot be carried successfully, id:"+_ps.id+"\t priority:"+_ps.priority);
		}
			
	}
	
	public void cleanAllConfigurations()
	{
		faultPacketServices.clear();
		bc.clearAll();
	}
	/*
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
						latency_1 += (_pp.getCurrentOccupiedBw()*_pp.carriedVTL.getPathLong());
						bw_1 += _pp.getCurrentOccupiedBw();
				}
				else if(_pp.priority == PacketService.PRIORITY_MID)
				{
					latency_2 += (_pp.getCurrentOccupiedBw()*_pp.carriedVTL.getPathLong());
					bw_2 += _pp.getCurrentOccupiedBw();
				}
			}			
		}
		System.out.println("\tlcy1 is\t"+decimalFormat.format(latency_1/bw_1/(3*100000))+"\tlcy2 is\t"+decimalFormat.format(latency_2/bw_2/(3*100000)));		
	}*/
	
	public void getBestEffortPakcetServiceLatencyStatistics(List<BestEffortPacketService> bepsl)
	{
		DecimalFormat decimalFormat=new DecimalFormat("0.000000");
		double latency_1 = 0.0;
		double latency_2 = 0.0;
		int bw_1 = 0;
		int bw_2 = 0;
		for(BestEffortPacketService _pp : bepsl)
		{
			if(_pp.carriedVTL!=null)
			{
				if(_pp.priority == PacketService.PRIORITY_HIGH)
				{
					if(_pp.carriedVTL!=null)
						latency_1 += (_pp.getCurrentOccupiedBw()*_pp.carriedVTL.getPathLong());
						bw_1 += _pp.getCurrentOccupiedBw();
				}
				else if(_pp.priority == PacketService.PRIORITY_MID)
				{
					latency_2 += (_pp.getCurrentOccupiedBw()*_pp.carriedVTL.getPathLong());
					bw_2 += _pp.getCurrentOccupiedBw();
				}
			}			
		}
		System.out.println("\tlcy1 is\t"+decimalFormat.format(latency_1/bw_1/(3*100000))+"\tlcy2 is\t"+decimalFormat.format(latency_2/bw_2/(3*100000)));		
	}
	
	public int showOccupiedBwStatisticsOfAllVTLS(List<VirtualTransLink> vtls)
	{
		int _bw_all = 0;
		
		for(VirtualTransLink vtl : vtls)
			_bw_all += vtl.getCapacity();
		System.out.println("Total occupied bw is :"+_bw_all+"\t"+"Total vtl count is :"+vtls.size());
		return _bw_all;		
	}

	public void showUtiliztionofPSs(int occupiedResource, List<PacketService> list) {
		
		DecimalFormat decimalFormat=new DecimalFormat("0.000");
		double utilization;
		int occupiedBW = 0;
		
		for(int i = 0; i < Service.TIME_BUCKET_NUM; i++)
		{
			occupiedBW = 0;
			for(PacketService ps : list)
			{
				occupiedBW += ps.getActualBwItUsed(i);
			}
			utilization = occupiedBW * 1.0/occupiedResource;
			System.out.print(decimalFormat.format(utilization) + "\t");
		}
		
		// TODO Auto-generated method stub
		
	}
	
	public void showCurrentUtiliztionofPSs(int occupiedResource, List<PacketService> list, int _time) {
		
		DecimalFormat decimalFormat=new DecimalFormat("0.000");
		double utilization;
		int occupiedBW = 0;
		
		occupiedBW = 0;
		for(PacketService ps : list)
		{
			occupiedBW += ps.getActualBwItUsed(_time);
		}
		utilization = occupiedBW * 1.0/occupiedResource;
		System.out.print(decimalFormat.format(utilization) + "\t");
		
		// TODO Auto-generated method stub
		
	}
}
