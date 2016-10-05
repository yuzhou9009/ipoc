package OFC;

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
import edu.bupt.ipoc.service.ServiceGenerator;
import edu.bupt.ipoc.service.VirtualTransLink;

public class OFC {
	public static void main(String[] args){
		OFC ofc = new OFC();
		//TestNo_Interactive ti = new TestNo_Interactive();
	}
	
	public OFC()
	{
		String s1 = "Data/OpticalTopology/3Point";//networkdouble";
		int packet_num =800;
		
		VariableGraph graph_G = new VariableGraph(s1);
		ServiceGenerator sg = new ServiceGenerator(graph_G);
		List<PacketService> psl = sg.random_PacketServices(packet_num, PacketService.D_TIME_DISTRIBUTION_ONLY,PacketService.INTERACTIVE);
		
		CentralizedController cc = new CentralizedController(graph_G);
		//CentralizedController cc = new CentralizedController(graph_G);
		
		int i = 0;
		
		for(PacketService ps : psl)
		{
			Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
			//consmp.put(Constraint.VTL_CARRY_C, new Constraint(Constraint.VTL_CARRY_C, VirtualTransLink.CAN_NOT_BE_EXTEND_BUT_SHARE, "The carried vtl cann't be extended!"));
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
		
		System.out.println("Can carry ps:"+i);
		
		//for(OpticalService )
		//int carried_ps = 0;
		
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
		/***********************************************************************/
		graph_G.clear_all_resource();
		
		cc.psm.clearAllServices();
		cc.osm.clearAllServices();
		cc.vtlm.clearAllServices();
		cc.otnsm.clearAllServices();
		
		
		
		/************************************************************************/
		
		
		int ps_used_bw = 0;
		i = 0;
		for(PacketService ps : psl)
		{
			Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
			
			if(cc.handleServiceRequest(ps, PacketService.CARRIED_REQUEST, consmp))
			{
			
				//System.out.print(i+":\tps id:"+ps.id+"\tpriority:"+ps.s_priority+"\tsouce:"+ps.sourceVertex+"\tdest"+ps.sinkVertex+"\tbandwidth:"+ps.getCurrentBw()+"\tcarried VTL id:"+ps.carriedVTL.id+"\t");//+ps.carriedVTL.relevantOTNServices.get(0).osBelongTo.path.toString());
				//System.out.println("\tstart slot:"+ps.carriedVTL.relevantOpticalServices.get(0).start_slots);
				//System.out.println();
				i++;
			}
		}
		
		System.out.println("22Can carry ps:"+i);
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
