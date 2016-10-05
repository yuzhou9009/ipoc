package IOC;

import java.util.List;
import java.util.Timer;
import java.util.Vector;

import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.bupt.ipoc.service.*;
import edu.bupt.ipoc.handler.OpticalResourceManager;
import edu.bupt.ipoc.managers.ServiceManager;
import edu.bupt.ipoc.resource.OpticalLink;

public class TestNo_Interactive {
	
	int false_count =0;
	int out = 0;
	
	String s1 = "Data/OpticalTopology/networkdouble";
	int packet_num = 500;
	
	
	VariableGraph graph_G = new VariableGraph(s1);
	ServiceGenerator sg = new ServiceGenerator(graph_G);
	
	OpticalResourceManager orm = new OpticalResourceManager(graph_G);
	ServiceManager  sm = new ServiceManager(graph_G,orm);
	
	public int sum_used_slot_num = 0;
	public int os_bw = 0;
	
	
	List<OpticalService> tem_os_l = new Vector<OpticalService>();
	public long[] sleep_times = new long[packet_num];
	
	public TestNo_Interactive(){


		List<PacketService> psl = sg.random_PacketServices(packet_num, PacketService.D_STATIC,ServiceManager.NON_INTERACTIVE);
		
		long sum_bw = 0;
		
		for(PacketService ps:psl)
		{
			ps.showMySelf();
			//sum_bw+=ps.getCurrentBw();
		}
		
		for(int i = 0;i < packet_num;i++)
		{
			sleep_times[i] = (int)(sg.P_rand(5)+1) * ServiceGenerator.SLEEP_BASE_NUM;
		}

		
		int tem = 0;
		for(PacketService ps_tem : psl)
		{
			try {
				Thread.sleep(sleep_times[tem++]);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(!sm.dealWithPacketService(ps_tem, 
					ServiceManager.CREAT_NEW_OPTIC_CHANNEL_IF_NEED,
					//OpticalService.SHARE_CHANNLE,
					OpticalService.NO_SHARE_CHANNEL,
					ServiceManager.ALLOCATE_WITH_MAX_BW,
					true))
			{
				false_count++;
				System.out.println("false");
			}
			else
			{
				sm._timer.schedule(sm.set_deletrafic_task(ps_tem), ps_tem.time_long);
				if(sm.total_packset_num%10 == 0)
					System.out.println("times:\t"+sm.total_packset_num+"\taverage time:\t"+sm.average_time);
			}
			//removeRightNow();
			checkOsState();
			if(packet_num/2 == sm.total_packset_num)
			{
				sum_used_slot_num = 0;
				for(OpticalLink rs : graph_G.get_vertex_pair_weight_index().values())
				{
					for(int i = 0;i<OpticalLink.SLOTS;i++)
					{
						if(rs.slots[i] != OpticalLink.FREE_FLAG)
							sum_used_slot_num++;				
					}		
				}
					
				os_bw = 0;
				for(OpticalService os_tem :sm.getOs_list())
				{
					//os_tem.showMyself();
					os_bw+=os_tem.getOccupiedChannelBw();
				}
			}
			
		}
		System.out.println("*************false_count:"+false_count+"\tused slot_num "+sum_used_slot_num+"\t used os_bw "+os_bw);
		System.out.println("Total slot num"+graph_G.get_vertex_pair_weight_index().size()*OpticalLink.SLOTS);
		sm._timer.cancel();
		System.out.println("new count"+sm.new_count);
		
//___________________________________//		
		tem = 0;
		false_count = 0;
		graph_G.clear_all_resource();
		sm.removeAllOpticalService();
		sm.average_time = 0.0;
		sm.total_packset_num = 0;
		sm.new_count = 0;
		sm._timer = new Timer();
		
		for(PacketService ps_tem : psl)
		{
			ps_tem.cleanMyself();
		}
		
		for(PacketService ps_tem : psl)
		{
			//long sleep_time = (int)(sg.P_rand(5)+1) * ServiceGenerator.SLEEP_BASE_NUM;
			try {
				Thread.sleep(sleep_times[tem++]);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(!sm.dealWithPacketService(ps_tem, 
					ServiceManager.CREAT_NEW_OPTIC_CHANNEL_IF_NEED,
					//OpticalService.SHARE_CHANNLE,
					OpticalService.NO_SHARE_CHANNEL,
					ServiceManager.ALLOCATE_WITH_MAX_BW,
					true))
			{
				false_count++;
				System.out.println("false");
			}
			else
			{
				sm._timer.schedule(sm.set_deletrafic_task(ps_tem), ps_tem.time_long);
				if(sm.total_packset_num%10 == 0)
					System.out.println("times:\t"+sm.total_packset_num+"\taverage time:\t"+sm.average_time);
			}
			removeRightNow();
			if(packet_num/2 == sm.total_packset_num)
			{
				sum_used_slot_num = 0;
				for(OpticalLink rs : graph_G.get_vertex_pair_weight_index().values())
				{
					for(int i = 0;i<OpticalLink.SLOTS;i++)
					{
						if(rs.slots[i] != OpticalLink.FREE_FLAG)
							sum_used_slot_num++;				
					}		
				}
					
				os_bw = 0;
				for(OpticalService os_tem :sm.getOs_list())
				{
					//os_tem.showMyself();
					os_bw+=os_tem.getOccupiedChannelBw();
				}
			}
			
		}
		//System.out.println("false_count:"+false_count);
		System.out.println("*************false_count:"+false_count+"\tused slot_num "+sum_used_slot_num+"\t used os_bw "+os_bw);
		System.out.println("new count"+sm.new_count);
		
/*		for(OpticalService os_tem :sm.getOs_list())
		{
			os_tem.showMyself();
			//os_bw+=os_tem.getOccupiedChannelBw();
		}*/
	}
	
	public void checkOsState()
	{
		if(sm.getOs_list().isEmpty())
		{
			System.out.println("Empty");
		}
		else
		{
			for(OpticalService os_tem :sm.getOs_list())
			{
				//os_tem.showMyself();
				//if(os_tem.getFreeTimeLong()>0)
					//System.out.println("free_time"+os_tem.getFreeTimeLong()+"\t the id is"+os_tem.id);
				if(os_tem.getFreeTimeLong() >= sm.MAX_FREE_TIME_LONG)
				{
					orm.handle_traffic(os_tem, orm.DELE, 0);
					tem_os_l.add(os_tem);
					System.out.println("arearerae");
				}
				//else if(os_tem.getFreeTimeLong() != 0)
				//{
					//System.out.printf("no service");
				//}
				//os_bw+=os_tem.getOccupiedChannelBw();
			}
			if(tem_os_l.size()>0)
				System.out.println("bbbbbbbbb:"+tem_os_l.size());
			for(OpticalService _oo : tem_os_l)
				sm.remove_OpticalService(_oo);
			tem_os_l.clear();
		}
		
	}
	
	
	public void removeRightNow()
	{
		if(sm.getOs_list().isEmpty())
		{
			System.out.println("Empty");
		}
		else
		{
			for(OpticalService os_tem :sm.getOs_list())
			{
				//os_tem.showMyself();
				//if(os_tem.getFreeTimeLong()>0)
					//System.out.println("free_time"+os_tem.getFreeTimeLong());
				if(os_tem.getFreeTimeLong() >0)
				{
					orm.handle_traffic(os_tem, orm.DELE, 0);
					tem_os_l.add(os_tem);
					//System.out.println("ddddddddd");
				}
				//else if(os_tem.getFreeTimeLong() != 0)
				//{
					//System.out.printf("no service");
				//}
				//os_bw+=os_tem.getOccupiedChannelBw();
			}
			if(tem_os_l.size()>0)
				System.out.println("ddddddddd:"+tem_os_l.size());
			for(OpticalService _oo : tem_os_l)
				sm.remove_OpticalService(_oo);
			tem_os_l.clear();
		}
		
	}
}
