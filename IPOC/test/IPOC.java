import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.CentralizedController;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.ServiceGenerator;
import edu.bupt.ipoc.service.VirtualTransLink;

public class IPOC {

	String topology = null;
	int packet_num;
	VariableGraph graph_G;
	ServiceGenerator sg;
	List<PacketService> psl;
	CentralizedController cc;
	
	public IPOC()
	{
		topology = "Data/OpticalTopology/nsfnetWeight";//networkdouble";
		packet_num = 3000;
		graph_G = new VariableGraph(topology);
		sg = new ServiceGenerator(graph_G);
		psl = sg.random_PacketServices(packet_num, ServiceGenerator.SERVICE_WITH_RANDOM_BW,ServiceGenerator.PERMENENT_SERVICE);
		cc = new CentralizedController(graph_G);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////	
	public static void main(String[] args){
		IPOC ipoc = new IPOC();
		//ipoc.showAllPacketServicesState();
		
		//ipoc.staticlyCarryingPacketServices();
		ipoc.carryingIndivisiblePacketServicesWithVTLBOD();
		
	}
	////////////////////////////////////////////////////////////////////////////////////////////	
	
	public void showAllPacketServicesState()
	{
		for(int j = 0;j<psl.size();j++)
			psl.get(j).showMyselfCurrentState();
	}
	
	private void staticlyCarryingPacketServices() {
		
		int tatal_successed_ps = 0;
		// traditional way. Carrying services according to the peek value* parameter(bigger than 1, for survivability) of services.
		for(PacketService ps : psl)
		{
			Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
			consmp.put(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, 
					new Constraint(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, PacketService.DYNAMICALLY_CARRIED_AND_INDIVISIBLE, "The ps will be carried in a satatic way!"));
			consmp.put(Constraint.VTL_CARRY_TYPE_C, 
					new Constraint(Constraint.VTL_CARRY_TYPE_C, VirtualTransLink.CAN_NOT_BE_EXTENDED_BUT_SHARED, "The vtl can not be extended but shared!"));
			
			if(cc.handleServiceRequest(ps, PacketService.CARRIED_REQUEST, consmp))
			{
				tatal_successed_ps++;
			}			
		}
		System.out.println("StaticlyCarrying: successed ps "+ tatal_successed_ps);
		//cc.vtlm.showAllVirtualTransLink();
		cc.sst.showOccupiedBwStatisticsOfAllVTLS(cc.vtlm.getAllVTLs());
		cc.sst.getPakcetServiceLatencyStatistics(psl);
		cc.sst.cleanAllConfigurations();
	}
	
	private void carryingIndivisiblePacketServicesWithVTLBOD()
	{
		int tatal_successed_ps = 0;
		
		for(PacketService ps : psl)
		{
			Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
			consmp.put(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, 
					new Constraint(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, PacketService.DYNAMICALLY_CARRIED_AND_DIVISIBLE, "The ps will be carried with vtl-bod!"));
			consmp.put(Constraint.VTL_CARRY_TYPE_C, 
					new Constraint(Constraint.VTL_CARRY_TYPE_C, VirtualTransLink.VTL_BOD, "The vtl can be extended and shared!"));
			if(cc.handleServiceRequest(ps, PacketService.CARRIED_REQUEST, consmp))
			{
				tatal_successed_ps++;
			}			
		}
		System.out.println("StaticlyCarrying: successed ps "+ tatal_successed_ps);
		//cc.vtlm.showAllVirtualTransLink();
		cc.sst.showOccupiedBwStatisticsOfAllVTLS(cc.vtlm.getAllVTLs());
		cc.sst.getPakcetServiceLatencyStatistics(psl);
		cc.sst.cleanAllConfigurations();
	}
}
	
	
	/*		int all_carried_bw = 0;
	for(PacketService ps : psl)
	{
		all_carried_bw+=ps.getCurrentBw();
	}		
	System.out.println("orignal Carried bw:"+all_carried_bw);
	
	for(PacketService ps : psl)
	{
		//ps.service_flag = PacketService.D_STATIC;
		//ps.parameter = 1.1764;
		Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
		consmp.put(Constraint.VTL_CARRY_TYPE_C, new Constraint(Constraint.VTL_CARRY_TYPE_C, VirtualTransLink.CAN_NOT_BE_EXTEND_BUT_SHARE, "The carried vtl cann't be extended!"));
		
		if(cc.handleServiceRequest(ps, PacketService.CARRIED_REQUEST, consmp))
		{
		
			//System.out.print(i+":\tps id:"+ps.id+"\tpriority:"+ps.s_priority+"\tsouce:"+ps.sourceVertex+"\tdest"+ps.sinkVertex+"\tbandwidth:"+ps.getCurrentBw()+"\tcarried VTL id:"+ps.carriedVTL.id+"\t");//+ps.carriedVTL.relevantOTNServices.get(0).osBelongTo.path.toString());
			//System.out.println("\tstart slot:"+ps.carriedVTL.relevantOpticalServices.get(0).start_slots);
			//System.out.println();
			i++;
		}
		//break;
	}
	
	System.out.print("11:Can carry ps:\t"+i);
	int occupied_bw = 0;
	
	for(OpticalService os : cc.osm.getAllOpticalService())
	{
		if(os.type == OpticalService.CHANNEL_10G_FOT_OTN)
			occupied_bw += (os.otn_children.size()* OTNService.BW_1G); 
		else
			occupied_bw += OpticalService.BW_10G;
	}
	//int carried_ps = 0;
	System.out.println("\tOccupied bw:\t"+occupied_bw);
	
	for(int time = 0; time<rolling_time;time++)
	{
		System.out.println(1+time*0.0001+"\t:uti\t"+ps_bw[time]*1.0/occupied_bw);
	}

	
	for(VirtualTransLink vtl : cc.vtlm.getAllVTLs())
	{
		//carried_ps +=vtl.carriedPacketServices.size();
		try{
			;//System.out.println(vtl.getPathLong());
		}catch(Exception e)
		{
			System.out.println("sdddddddddddddd");
		}
		
	}
	cc.sst.getPakcetServiceLatencyStatistics(psl);
	cc.sst.cleanAllConfigurations();
	*/
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
/*		for(int time = 0; time<rolling_time;time++)
	{
		
		
		
		int ps_used_bw = 0;
		i = 0;
		for(PacketService ps : psl)
		{
			ps.bucket_count = time;
			ps.service_flag = PacketService.D_TIME_DISTRIBUTION_ONLY;
			ps.parameter = 1.1764;
			Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
			consmp.put(Constraint.VTL_CARRY_C, new Constraint(Constraint.VTL_CARRY_C, VirtualTransLink.CAN_NOT_BE_EXTEND_BUT_SHARE, "The carried vtl cann't be extended!"));
						
			if(cc.handleServiceRequest(ps, PacketService.CARRIED_REQUEST, consmp))
			{
			
				//System.out.print(i+":\tps id:"+ps.id+"\tpriority:"+ps.s_priority+"\tsouce:"+ps.sourceVertex+"\tdest"+ps.sinkVertex+"\tbandwidth:"+ps.getCurrentBw()+"\tcarried VTL id:"+ps.carriedVTL.id+"\t");//+ps.carriedVTL.relevantOTNServices.get(0).osBelongTo.path.toString());
				//System.out.println("\tstart slot:"+ps.carriedVTL.relevantOpticalServices.get(0).start_slots);
				//System.out.println();
				i++;
			}
		}
		
		System.out.print((2+0.0001*time)+"\t:Can carry ps:\t"+i);
		
		occupied_bw = 0;
		
		for(OpticalService os : cc.osm.getAllOpticalService())
		{
			if(os.type == OpticalService.CHANNEL_10G_FOT_OTN)
				occupied_bw += (os.otn_children.size()* OTNService.BW_1G); 
			else
				occupied_bw += OpticalService.BW_10G;
		}
		//int carried_ps = 0;

		DecimalFormat decimalFormat=new DecimalFormat("0.000000");
		System.out.print("\tOccupied bw:\t"+occupied_bw+"\t uti:\t"+decimalFormat.format(ps_bw[time]*1.0/occupied_bw));
		
		cc.sst.getPakcetServiceLatencyStatistics(psl);			
		cc.sst.cleanAllConfigurations();
	}
*/		
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*		
	for(int time = 0; time<rolling_time;time++)
	{
		//int ps_used_bw = 0;
		i = 0;
				
		for(PacketService ps : psl)
		{
			ps.service_flag = PacketService.D_TIME_DISTRIBUTION_ONLY;
			ps.parameter = 1.1764;
			ps.bucket_count = time;
			Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
			
			if(cc.handleServiceRequest(ps, PacketService.CARRIED_REQUEST, consmp))
			{
				//System.out.print(i+":\tps id:"+ps.id+"\tpriority:"+ps.s_priority+"\tsouce:"+ps.sourceVertex+"\tdest"+ps.sinkVertex+"\tbandwidth:"+ps.getCurrentBw()+"\tcarried VTL id:"+ps.carriedVTL.id+"\t");//+ps.carriedVTL.relevantOTNServices.get(0).osBelongTo.path.toString());
				//System.out.println("\tstart slot:"+ps.carriedVTL.relevantOpticalServices.get(0).start_slots);
				//System.out.println();
				i++;
			}
		}
		
		System.out.print((3+0.0001*time)+"\t:Can carry ps:\t"+i);
		
		occupied_bw = 0;
		
		for(OpticalService os : cc.osm.getAllOpticalService())
		{
			if(os.type == OpticalService.CHANNEL_10G_FOT_OTN)
				occupied_bw += (os.otn_children.size()* OTNService.BW_1G); 
			else
				occupied_bw += OpticalService.BW_10G;
		}

		DecimalFormat decimalFormat=new DecimalFormat("0.000000");
		System.out.print("\tOccupied bw:\t"+occupied_bw+"\t uti:\t"+decimalFormat.format(ps_bw[time]*1.0/occupied_bw));
		

		cc.sst.getPakcetServiceLatencyStatistics(psl);
		cc.sst.cleanAllConfigurations();		
	}
*/
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	//System.out.println();
/*		
	for(int time = 0; time<rolling_time;time++)
	{
		i = 0;
		List<PacketService> sort_ps_1 = new ArrayList<PacketService>();
		List<PacketService> sort_ps_2 = new ArrayList<PacketService>();
		List<PacketService> sort_ps_3 = new ArrayList<PacketService>();
		
		List<PacketService> tem_ps = new ArrayList<PacketService>();
		
		for(PacketService ps : psl)
		{
			if(ps.s_priority == PacketService.SP_HIGH)// || ps.s_priority  == PacketService.SP_MID )
			{
				sort_ps_1.add(ps);
			}
			else if(ps.s_priority  == PacketService.SP_MID )
				sort_ps_2.add(ps);
			else
				sort_ps_3.add(ps);
		}
		
		//System.out.println("There are priority 3 is :"+sort_ps_3.size());
		sort_ps_1.addAll(sort_ps_2);
		sort_ps_1.addAll(sort_ps_3);
		
		for(PacketService ps : sort_ps_1)
		{
			ps.service_flag = PacketService.D_TIME_DISTRIBUTION_ONLY;
			ps.parameter = 1.0;
			ps.bucket_count = time;
			Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
			consmp.put(Constraint.VTL_CARRY_TYPE_C, new Constraint(Constraint.VTL_CARRY_TYPE_C, PacketService.VTL_BOD, "The ps will be carried with bod!"));
			
			if(cc.handleServiceRequest(ps, PacketService.CARRIED_REQUEST, consmp))
			{
			
				//System.out.print(i+":\tps id:"+ps.id+"\tpriority:"+ps.s_priority+"\tsouce:"+ps.sourceVertex+"\tdest"+ps.sinkVertex+"\tbandwidth:"+ps.getCurrentBw()+"\tcarried VTL id:"+ps.carriedVTL.id+"\t");//+ps.carriedVTL.relevantOTNServices.get(0).osBelongTo.path.toString());
				//System.out.println("\tstart slot:"+ps.carriedVTL.relevantOpticalServices.get(0).start_slots);
				//System.out.println();
				i++;
			}
		}
		
		System.out.print((4+0.0001*time)+"\t:Can carry ps:\t"+i);
		
		occupied_bw = 0;
		
		for(OpticalService os : cc.osm.getAllOpticalService())
		{
			if(os.type == OpticalService.CHANNEL_10G_FOT_OTN)
				occupied_bw += (os.otn_children.size()* OTNService.BW_1G); 
			else
				occupied_bw += OpticalService.BW_10G;
		}
		//int carried_ps = 0;
		DecimalFormat decimalFormat=new DecimalFormat("0.000000");
		System.out.print("\tOccupied bw:\t"+occupied_bw+"\t uti:\t"+decimalFormat.format(ps_bw[time]*1.0/occupied_bw));
		
		cc.sst.getPakcetServiceLatencyStatistics(psl);
		
		//if(time == 239)
		//{
		//	for(VirtualTransLink vtt : cc.vtlm.getAllVTLs())
		//	{
		//		System.out.println(vtt.getUsedBWofVTL()*1.0/vtt.bw_capacity);
		//	}
		//}			
		cc.sst.cleanAllConfigurations();
	}
*/		
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////		
/*		i = 0;
	List<PacketService> sort_ps_1 = new ArrayList<PacketService>();
	List<PacketService> sort_ps_2 = new ArrayList<PacketService>();
	List<PacketService> sort_ps_3 = new ArrayList<PacketService>();
	
	//List<PacketService> tem_ps = new ArrayList<PacketService>();
	
	for(PacketService ps : psl)
	{
		if(ps.s_priority == PacketService.PRIORITY_HIGH)// || ps.s_priority  == PacketService.SP_MID )
		{
			sort_ps_1.add(ps);
		}
		else if(ps.s_priority  == PacketService.PRIORITY_MID )
			sort_ps_2.add(ps);
		else
			sort_ps_3.add(ps);
	}
	
	//System.out.println("There are priority 3 is :"+sort_ps_3.size());
	sort_ps_1.addAll(sort_ps_2);
	sort_ps_1.addAll(sort_ps_3);
	
	for(PacketService ps : sort_ps_1)
	{
		ps.service_flag = PacketService.D_TIME_DISTRIBUTION_ONLY;
		ps.parameter = 1.0;
		ps.bucket_count = 0;
		Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
		consmp.put(Constraint.VTL_CARRY_TYPE_C, new Constraint(Constraint.VTL_CARRY_TYPE_C, PacketService.VTL_BOD, "The ps will be carried with bod!"));
		
		if(cc.handleServiceRequest(ps, PacketService.CARRIED_REQUEST, consmp))
		{
		
			//System.out.print(i+":\tps id:"+ps.id+"\tpriority:"+ps.s_priority+"\tsouce:"+ps.sourceVertex+"\tdest"+ps.sinkVertex+"\tbandwidth:"+ps.getCurrentBw()+"\tcarried VTL id:"+ps.carriedVTL.id+"\t");//+ps.carriedVTL.relevantOTNServices.get(0).osBelongTo.path.toString());
			//System.out.println("\tstart slot:"+ps.carriedVTL.relevantOpticalServices.get(0).start_slots);
			//System.out.println();
			i++;
		}
	}
	System.out.print((4+0.0001*1)+"\t:Can carry ps:\t"+i);
	
	for(int time = 1; time<rolling_time;time++)
	{
		for(PacketService ps : sort_ps_1)
		{
			ps.current_bucket_count = time;
		}
		
		for(List<VirtualTransLink> _vtls : cc.vtlm.getVTLMAP().values())
		{
			for(VirtualTransLink _vtl : _vtls)
			{
				int _request = _vtl.needCapacityAdjustment();
				if(_request == VirtualTransLink.NEED_TO_BE_EXTENDED)
				{
					if(cc.handleServiceRequest(_vtl, VirtualTransLink.EXTEND_REQUEST, null))
					{
						;
					}
					else
						System.out.println("Adjustment failed!");
				}
				else if(_request == VirtualTransLink.NEED_TO_BE_SHRINKED)
				{
					if(cc.handleServiceRequest(_vtl, VirtualTransLink.SHRINKED_REQUEST, null))
					{
						;
					}
				}
			}				
		}			
	}
	
	
	
*/		
	
	
	//int ps_used_bw = 0;
	
	
	
//	all_carried_bw = 0;
//	for(VirtualTransLink vtl: cc.vtlm.getAllVTLs())
//	{
///			all_carried_bw += vtl.getUsedBWofVTL();
//	}
//	System.out.println("Carried bw:"+all_carried_bw);
	//System.out.println(carried_ps);
	
/*		sm.setPacketServiceManager(psm);
	
	PacketServiceRequestHandler psrh = new PacketServiceRequestHandler(ServiceManage);
	
	OpticalResourceRequestHandler orrh = new OpticalResourceRequestHandler(graph_G);
	OpticalService os1 = sg.create_OpticalService();
	
	orrh.handlerRequest(os1, 0, null);
	
	System.out.println(""+os1.path);
*/		
	
	/*
	for(PacketService ps:psl)
		ps.showMySelfwithStaitcBW();
	
	OpticalResourceRequestHandler orrh = new OpticalResourceRequestHandler(graph_G);
	VTLManager vtlm = new VTLManager(orm);
	ServiceManager  sm = new ServiceManager(graph_G,vtlm,orm);s
	
	System.out.println("In topology:"+s1);
	long sum_bw = 0;
	for(PacketService ps_tem : psl)
	{
		sum_bw+= ps_tem.static_bw;
	}

	System.out.println("Packet Service num:"+packet_num+".The real sum static bw is :"+sum_bw);

	int sum_used_slot_num = 0;
	int false_count =0;
	long os_bw = 0;
	
//Init end
//_____________________________________//
	
	//VTL and use current dynamic bw once(without adjustment)
	
	for(PacketService ps_tem : psl)
	{
		sm.dealWithPacketService(ps_tem, 
				create_new_optic_channel_flag,
				_share_channel_flag,
				_static_flag, 
				ServiceManager.ALLOCATE_RESOURCE);
		if(!sm.allocatePacketService(ps_tem, 
				ServiceManager.CREAT_NEW_OPTIC_CHANNEL_IF_NEED,
				//OpticalService.SHARE_CHANNLE,
				OpticalService.NO_SHARE_CHANNEL,
				ServiceManager.ALLOCATE_WITH_MAX_BW))
		{
			System.out.println("ps_id:"+ps_tem.id);
			false_count++;
		}				
	}
	*/
	
	
//}
	
