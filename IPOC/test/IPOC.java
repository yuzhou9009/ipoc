import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.CentralizedController;
import edu.bupt.ipoc.service.BandwidthTolerantPacketService;
import edu.bupt.ipoc.service.BestEffortPacketService;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.ServiceGenerator;
import edu.bupt.ipoc.service.VirtualTransLink;

public class IPOC {

	String topology = null;
	int be_packet_num;
	int bt_packet_num;
	int one_day_time_slice;
	int test_days;
	int time_multiple;
	VariableGraph graph_G;
	ServiceGenerator sg;
	List<BestEffortPacketService> bepsl;
	List<BandwidthTolerantPacketService> btpsl;
	int[] time_interval_list = null;
	CentralizedController cc;//BasicController
	
	public IPOC()
	{
		topology = "Data/OpticalTopology/3Point";//nsfnetWeight";//networkdouble";
		be_packet_num = 100;
		bt_packet_num = 10000;
		one_day_time_slice = 24*60*60/Service.TIME_STEP;
		time_multiple = one_day_time_slice/Service.TIME_BUCKET_NUM;
		test_days = 5;
				
		graph_G = new VariableGraph(topology);
		sg = new ServiceGenerator(graph_G);
		
		bepsl = sg.random_BestEffortPacketServices(be_packet_num, ServiceGenerator.SERVICE_WITH_RANDOM_BW);
		btpsl = sg.random_BandwidthTolerantPacketService(bt_packet_num, 0);
		time_interval_list = sg.random_TimeIntervalList(bt_packet_num);
		
		cc = new CentralizedController(graph_G);
		cc.f_debug_info = false;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////	
	public static void main(String[] args){
		IPOC ipoc = new IPOC();
		//ipoc.showAllPacketServicesState();
		
		//ipoc.oneTimeStaticlyCarryingPacketServices();
		//ipoc.cc.sst.showOccupiedBwStatisticsOfAllVTLS(ipoc.cc.vtlm.getAllVTLs());
		//ipoc.cc.sst.getPakcetServiceLatencyStatistics(ipoc.psl);
		//ipoc.cc.sst.cleanAllConfigurations();
		
		System.out.println("****************************Begin***************************************");
		ipoc.oneTimeCarryingDivisiblePacketServicesWithVTLBOD();
		ipoc.cc.sst.showOccupiedBwStatisticsOfAllVTLS(ipoc.cc.vtlm.getAllVTLs());
		ipoc.cc.sst.getBestEffortPakcetServiceLatencyStatistics(ipoc.bepsl);
		ipoc.cc.sst.cleanAllConfigurations();
		System.out.println("\n*****************************End****************************************");
		
		System.out.println("****************************Begin***************************************");
		ipoc.LongTimeCarryingIndivisiblePacketServicesWithVTLBOD();		
		
	}
	////////////////////////////////////////////////////////////////////////////////////////////	
	

	public void showAllPacketServicesState()
	{
		for(int j = 0;j<bepsl.size();j++)
			bepsl.get(j).showMyselfCurrentState();
	}
	
	private void oneTimeStaticlyCarryingPacketServices() {
		
		int tatal_successed_ps = 0;
		// traditional way. Carrying services according to the peek value* parameter(bigger than 1, for survivability) of services.
		for(BestEffortPacketService ps : bepsl)
		{
			Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
			consmp.put(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, 
					new Constraint(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, PacketService.STATIC_CARRIED, "The ps will be carried in a satatic way!"));
			consmp.put(Constraint.VTL_CARRY_TYPE_C, 
					new Constraint(Constraint.VTL_CARRY_TYPE_C, VirtualTransLink.STATIC_BUT_SHARED, "The vtl can not be extended but shared!"));
			
			if(cc.handleServiceRequest(ps, Service.PS_CARRIED_REQUEST, consmp))
			{
				tatal_successed_ps++;
			}			
		}
		System.out.println("StaticlyCarrying: successed ps "+ tatal_successed_ps);
		//cc.vtlm.showAllVirtualTransLink();
	}
	
	private void oneTimeCarryingDivisiblePacketServicesWithVTLBOD()
	{
		int tatal_successed_ps = 0;
		
		for(BestEffortPacketService ps : bepsl)
		{
			Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
			consmp.put(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, 
					new Constraint(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, PacketService.DYNAMICALLY_CARRIED_AND_DIVISIBLE, "The ps will be carried with vtl-bod!"));
			consmp.put(Constraint.VTL_CARRY_TYPE_C, 
					new Constraint(Constraint.VTL_CARRY_TYPE_C, VirtualTransLink.VTL_BOD, "The vtl can be extended and shared!"));
			if(cc.handleServiceRequest(ps, Service.PS_CARRIED_REQUEST, consmp))
			{
				tatal_successed_ps++;
			}			
		}
		System.out.println("VTLBOD Carrying: successed ps "+ tatal_successed_ps);
	}
	
	private void LongTimeCarryingIndivisiblePacketServicesWithVTLBOD() {

		oneTimeCarryingDivisiblePacketServicesWithVTLBOD();
		System.out.print(0+":");
		cc.sst.showOccupiedBwStatisticsOfAllVTLS(cc.vtlm.getAllVTLs());
		
		int count_for_next_service = 0;
		int bt_service_count = 0;
		int be_time = 0;
		
		for(int t_day = 0; t_day < test_days; t_day++)
		{
			be_time = 0;
			for(int time_slice_count = 0; time_slice_count < one_day_time_slice; time_slice_count++)
			{
				//for all running BT service, check their state
				//If any service is out of date, remove it. do the adjustment.
				//cc.em.checkEventList();
				if(t_day > 0)
				{
					cc.psm.updateBTServicesStatue();
					
					if(count_for_next_service == 0)
					{
						if(bt_service_count < bt_packet_num)
						{
							Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
							consmp.put(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, 
									new Constraint(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, PacketService.DYNAMICALLY_CARRIED_AND_DIVISIBLE, "The ps will be carried with vtl-bod!"));
							consmp.put(Constraint.VTL_CARRY_TYPE_C, 
									new Constraint(Constraint.VTL_CARRY_TYPE_C, VirtualTransLink.VTL_BOD, "The vtl can be extended and shared!"));
							
							cc.handleServiceRequest(btpsl.get(bt_service_count), Service.PS_CARRIED_REQUEST, consmp);
							count_for_next_service = time_interval_list[bt_service_count];
							count_for_next_service --;
							bt_service_count++;
						}
						else if(bt_service_count == bt_packet_num)
						{
							System.out.println("t_day:"+t_day+" be_time:"+be_time);
							bt_service_count++;
						}
					}
					else
						count_for_next_service--;
				}

				if((time_slice_count % time_multiple) == 0)
				{
					for(BestEffortPacketService ps : bepsl)
					{
						ps.current_bucket_count = be_time;
					}
					
					cc.checkVTLStatue();
					//System.out.print(be_time+":");
					cc.sst.showOccupiedBwStatisticsOfAllVTLS(cc.vtlm.getAllVTLs());
					//if(t_day == test_days-1 && (be_time == 20 || be_time == 200))
					//{
					//	cc.vtlm.showAllVirtualTransLink();
					//}
					be_time++;			
				}
			}
		}
	}
	
}
	
