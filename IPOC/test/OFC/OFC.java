package OFC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import IOC.IocMain;
import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.controller.CentralizedController;
import edu.bupt.ipoc.handler.OpticalResourceRequestHandler;
import edu.bupt.ipoc.managers.PacketServiceManager;
import edu.bupt.ipoc.managers.ServiceManager;
import edu.bupt.ipoc.service.OTNService;
import edu.bupt.ipoc.service.OpticalService;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.PacketServiceChild;
import edu.bupt.ipoc.service.ServiceGenerator;
import edu.bupt.ipoc.service.VirtualTransLink;

public class OFC {
	public static void main(String[] args){
		OFC ofc = new OFC();
		//TestNo_Interactive ti = new TestNo_Interactive();
	}
	
	public OFC()
	{
		String s1 = "Data/OpticalTopology/networkdouble";//networkdouble";
		int packet_num =5000;
		
		VariableGraph graph_G = new VariableGraph(s1);
		ServiceGenerator sg = new ServiceGenerator(graph_G);
		List<PacketService> psl = sg.random_PacketServices(packet_num, PacketService.D_TIME_DISTRIBUTION_ONLY,PacketService.INTERACTIVE);
		
		CentralizedController cc = new CentralizedController(graph_G);
		//CentralizedController cc = new CentralizedController(graph_G);
		
		int i = 0;
		int rolling_time =  1;//PacketService.TIME_BUCKET_NUM;
		
		int all_carried_bw = 0;
		for(PacketService ps : psl)
		{
			all_carried_bw+=ps.getCurrentBw();
		}		
		System.out.println("orignal Carried bw:"+all_carried_bw);
		
		for(PacketService ps : psl)
		{
			ps.service_flag = PacketService.D_STATIC;
			Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
			consmp.put(Constraint.VTL_CARRY_C, new Constraint(Constraint.VTL_CARRY_C, VirtualTransLink.CAN_NOT_BE_EXTEND_BUT_SHARE, "The carried vtl cann't be extended!"));
			//consmp.put(Constraint.PS_CARRIED_TYPE, new Constraint(Constraint.PS_CARRIED_TYPE, PacketService.STATIC_CARRIED, "The ps carried with static bw!"));
			
			if(cc.handleServiceRequest(ps, PacketService.CARRIED_REQUEST, consmp))
			{
			
				//System.out.print(i+":\tps id:"+ps.id+"\tpriority:"+ps.s_priority+"\tsouce:"+ps.sourceVertex+"\tdest"+ps.sinkVertex+"\tbandwidth:"+ps.getCurrentBw()+"\tcarried VTL id:"+ps.carriedVTL.id+"\t");//+ps.carriedVTL.relevantOTNServices.get(0).osBelongTo.path.toString());
				//System.out.println("\tstart slot:"+ps.carriedVTL.relevantOpticalServices.get(0).start_slots);
				//System.out.println();
				i++;
			}
			//break;
		}
		
		System.out.print("11:Can carry ps:"+i);
		int occupied_bw = 0;
		
		for(OpticalService os : cc.osm.getAllOpticalService())
		{
			if(os.type == OpticalService.CHANNEL_10G_FOT_OTN)
				occupied_bw += (os.otn_children.size()* OTNService.BW_1G); 
			else
				occupied_bw += OpticalService.BW_10G;
		}
		//int carried_ps = 0;
		System.out.println("Occupied bw:"+occupied_bw);
		
		for(VirtualTransLink vtl : cc.vtlm.getAllVTLs())
		{
			//carried_ps +=vtl.carriedPacketServices.size();
			try{
				;//System.out.println(vtl);
			}catch(Exception e)
			{
				System.out.println("sdddddddddddddd");
				/*
				if(vtl.relevantOTNServices.size()>0)
				{
					for(OTNService otn: vtl.relevantOTNServices)
					{
						if(otn.osBelongTo == null)
							System.out.println("The wrong otn id is:"+otn.id);
					}
				}*/
			}
			
		}
		System.out.println();
		
		/***********************************************************************/
		graph_G.clear_all_resource();
		
		cc.psm.clearAllServices();
		cc.osm.clearAllServices();
		cc.vtlm.clearAllServices();
		cc.otnsm.clearAllServices();
		
		
		
		/************************************************************************/
		
		for(int time = 0; time<rolling_time;time++)
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
			
			System.out.print("22:"+time+":Can carry ps:"+i);
			
			occupied_bw = 0;
			
			for(OpticalService os : cc.osm.getAllOpticalService())
			{
				if(os.type == OpticalService.CHANNEL_10G_FOT_OTN)
					occupied_bw += (os.otn_children.size()* OTNService.BW_1G); 
				else
					occupied_bw += OpticalService.BW_10G;
			}
			//int carried_ps = 0;
			System.out.println("\tOccupied bw:"+occupied_bw);
					
			
			
			
			
			/***********************************************************************/
			graph_G.clear_all_resource();
			
			cc.psm.clearAllServices();
			cc.osm.clearAllServices();
			cc.vtlm.clearAllServices();
			cc.otnsm.clearAllServices();
			
			
			
			/************************************************************************/
		}
		
		
		System.out.println();
		////////////////////////////////////////////////////////////
		
		for(int time = 0; time<rolling_time;time++)
		{
			//int ps_used_bw = 0;
			i = 0;
					
			for(PacketService ps : psl)
			{
				ps.service_flag = PacketService.D_TIME_DISTRIBUTION_ONLY;
				ps.parameter = 1.0;
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
			
			System.out.print("33:"+time+"Can carry ps:"+i);
			
			occupied_bw = 0;
			
			for(OpticalService os : cc.osm.getAllOpticalService())
			{
				if(os.type == OpticalService.CHANNEL_10G_FOT_OTN)
					occupied_bw += (os.otn_children.size()* OTNService.BW_1G); 
				else
					occupied_bw += OpticalService.BW_10G;
			}
			//int carried_ps = 0;
			System.out.println("\tOccupied bw:"+occupied_bw);
			
			
			
			
			/***********************************************************************/
			graph_G.clear_all_resource();
			
			cc.psm.clearAllServices();
			cc.osm.clearAllServices();
			cc.vtlm.clearAllServices();
			cc.otnsm.clearAllServices();
			
			
			
			/************************************************************************/
			
		}
		
		System.out.println();
		
		for(int time = 0; time<rolling_time;time++)
		{
			i = 0;
			List<PacketService> sort_ps_1 = new ArrayList<PacketService>();
			List<PacketService> sort_ps_2 = new ArrayList<PacketService>();
			List<PacketService> sort_ps_3 = new ArrayList<PacketService>();
			
			List<PacketService> tem_ps = new ArrayList<PacketService>();
			
			for(PacketService ps : psl)
			{
				if(ps.s_priority == PacketService.SP_HIGH || ps.s_priority== PacketService.SP_MID)
				{
					sort_ps_1.add(ps);
				}
				//else if(ps.s_priority )
				//	sort_ps_2.add(ps);
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
				consmp.put(Constraint.VTL_CARRY_C, new Constraint(Constraint.VTL_CARRY_C, VirtualTransLink.ADVANCED_BOD, "The ps will be carried with bod!"));
				//consmp.put(Constraint.PS_CARRIED_TYPE, new Constraint(Constraint.PS_CARRIED_TYPE, PacketService.ADVANCED_BOD, "The ps will be carried with bod!"));
				
				
				if(cc.handleServiceRequest(ps, PacketService.CARRIED_REQUEST, consmp))
				{
				
					//System.out.print(i+":\tps id:"+ps.id+"\tpriority:"+ps.s_priority+"\tsouce:"+ps.sourceVertex+"\tdest"+ps.sinkVertex+"\tbandwidth:"+ps.getCurrentBw()+"\tcarried VTL id:"+ps.carriedVTL.id+"\t");//+ps.carriedVTL.relevantOTNServices.get(0).osBelongTo.path.toString());
					//System.out.println("\tstart slot:"+ps.carriedVTL.relevantOpticalServices.get(0).start_slots);
					//System.out.println();
					i++;
				}
			}
			
			System.out.print("44:"+time+"Can carry ps:"+i);
			
			occupied_bw = 0;
			
			for(OpticalService os : cc.osm.getAllOpticalService())
			{
				if(os.type == OpticalService.CHANNEL_10G_FOT_OTN)
					occupied_bw += (os.otn_children.size()* OTNService.BW_1G); 
				else
					occupied_bw += OpticalService.BW_10G;
			}
			//int carried_ps = 0;
			System.out.println("\tOccupied bw:"+occupied_bw);
			
			/***********************************************************************/
			graph_G.clear_all_resource();
			
			cc.psm.clearAllServices();
			cc.osm.clearAllServices();
			cc.vtlm.clearAllServices();
			cc.otnsm.clearAllServices();
			
			
			
			/************************************************************************/
		}
		
		
		//int ps_used_bw = 0;
		
		
		
//		all_carried_bw = 0;
//		for(VirtualTransLink vtl: cc.vtlm.getAllVTLs())
//		{
///			all_carried_bw += vtl.getUsedBWofVTL();
//		}
//		System.out.println("Carried bw:"+all_carried_bw);
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
		
		
	}
	
	
}
