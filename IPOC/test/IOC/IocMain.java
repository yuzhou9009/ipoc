package IOC;
//This is a program for IP and Optical Convergence

import java.util.List;
import java.util.Vector;

import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.bupt.ipoc.service.*;
import edu.bupt.ipoc.handler.OpticalResourceManager;
import edu.bupt.ipoc.managers.ServiceManager;
import edu.bupt.ipoc.resource.OpticalLink;

public class IocMain {
	
	public static void main(String[] args){
		IocMain im = new IocMain();
		//TestNo_Interactive ti = new TestNo_Interactive();
	}
	
	public IocMain(){


		//Init Start		
				String s1 = "Data/OpticalTopology/networkdouble";
				int packet_num = 2000;
				
				VariableGraph graph_G = new VariableGraph(s1);
				ServiceGenerator sg = new ServiceGenerator(graph_G);
				List<PacketService> psl = sg.random_PacketServices(packet_num, PacketService.D_TIME_DISTRIBUTION_ONLY,ServiceManager.INTERACTIVE);
				
				for(PacketService ps:psl)
					ps.showMySelf();
				
				OpticalResourceManager orm = new OpticalResourceManager(graph_G);
				ServiceManager  sm = new ServiceManager(graph_G,orm);
				

/*				
				ResourceManager rm = new ResourceManager(graph_G);
				c
				
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
		//No Share_Channel and STATIC BW test
				for(PacketService ps_tem : psl)
				{
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
				
				System.out.println("With staticly allocation according to static bw,the false_count is:"+false_count);
				for(Resource rs : graph_G.get_vertex_pair_weight_index().values())
				{
					for(int i = 0;i<Resource.SLOTS;i++)
					{
						if(rs.slots[i] != Resource.FREE_FLAG)
							sum_used_slot_num++;				
					}		
				}
							
				for(OpticalService os_tem :sm.getOs_list())
				{
//					os_tem.showMyself();
					os_bw+=os_tem.getOccupiedChannelBw();
				}
				
		System.out.println("The used slots num is:\t"+sum_used_slot_num+"\t.The whole slot num is \t"+graph_G.get_vertex_pair_weight_index().size()*Resource.SLOTS+"\t.The occupied bw is \t"+os_bw);

		//___________________________________//	
		//SHARE_CHANNEL and use current dynamic bw	once
				graph_G.clear_all_resource();
				sm.getOs_list().clear();
				sm.get_vertex_pair_os_list_index().clear();

						
				for(PacketService ps_tem : psl)
				{
					//ps_tem.static_bw = PacketService.TIME_DISTRIBUTION;
					ps_tem.cleanMyself();
				}
				
				long sum_bw_tem = 0;
				false_count =0;
				for(PacketService ps_tem : psl)
				{
					sum_bw_tem += ps_tem.real_time_bw[ps_tem.bucket_count];
					if(!sm.allocatePacketService(ps_tem, 
							ServiceManager.CREAT_NEW_OPTIC_CHANNEL_IF_NEED,
							OpticalService.SHARE_CHANNLE,
							ServiceManager.ALLOCATE_WITH_MAX_BW))
						false_count++;		
				}		
				
				System.out.println("\nWith dynamiclly allocation according to static bw,the false_count is:"+false_count);
				sum_used_slot_num = 0;
				for(Resource rs : graph_G.get_vertex_pair_weight_index().values())
				{
					for(int i = 0;i<Resource.SLOTS;i++)
					{
						if(rs.slots[i] != Resource.FREE_FLAG)
							sum_used_slot_num++;				
					}		
				}
					
				os_bw = 0;
				for(OpticalService os_tem :sm.getOs_list())
				{
					//os_tem.showMyself();
					os_bw+=os_tem.getOccupiedChannelBw();
				}
				
		System.out.println("The used slots num is:\t"+sum_used_slot_num+"The real used bw is:\t"+sum_bw_tem+"\t.The whole slot num is \t"+graph_G.get_vertex_pair_weight_index().size()*Resource.SLOTS+"\t.The occupied bw is \t"+os_bw);
			


		//___________________________________//	
		//SHARE_CHANNEL and use current dynamic bw	TIME_BUCKET_NUM times, without dynamiclly adjust

				int[] false_count_list = new int[PacketService.TIME_BUCKET_NUM];
				int[] sum_used_slot_num_list = new int[PacketService.TIME_BUCKET_NUM];
				long[] sum_bw_list = new long[PacketService.TIME_BUCKET_NUM];
				long[] sum_occupied_bw_list = new long[PacketService.TIME_BUCKET_NUM];
				for(int j = 0;j<PacketService.TIME_BUCKET_NUM;j++)
				{
					graph_G.clear_all_resource();
					sm.getOs_list().clear();
					sm.get_vertex_pair_os_list_index().clear();
					for(PacketService ps_tem : psl)
					{
						//ps_tem.static_bw = PacketService.TIME_DISTRIBUTION;
						ps_tem.cleanMyselfWithoutBucketCount();
						//ps_tem.father_os = null;
						//ps_tem.father_os_sub_os =null;
					}
					sum_bw_list[j] = 0;
					for(PacketService ps_tem : psl)
					{
						//sum_bw_list[j]+= ps_tem.real_time_bw[j];
						sum_bw_list[j]+= ps_tem.real_time_bw[ps_tem.bucket_count];
					}
					
					false_count_list[j] =0;
					for(PacketService ps_tem : psl)
					{
						//ps_tem.static_bw = 0;
						//ps_tem.
						if(!sm.allocatePacketService(ps_tem, 
								ServiceManager.CREAT_NEW_OPTIC_CHANNEL_IF_NEED,
								OpticalService.SHARE_CHANNLE,
								ServiceManager.ALLOCATE_WITH_CURRENT_BW))
							false_count_list[j]++;
						ps_tem.addStepBucketCount();
					
					}
					sum_used_slot_num_list[j] = 0;
					for(Resource rs : graph_G.get_vertex_pair_weight_index().values())
					{
						for(int i = 0;i<Resource.SLOTS;i++)
						{
							if(rs.slots[i] != Resource.FREE_FLAG)
								sum_used_slot_num_list[j]++;
							
						}		
					}
					
					sum_occupied_bw_list[j] = 0;
					for(OpticalService os_tem :sm.getOs_list())
					{
						//os_tem.showMyself();
						sum_occupied_bw_list[j]+=os_tem.getOccupiedChannelBw();
					}
					//if(j%40 == 0)
					//	System.out.println("Now "+j);
					//System.out.println("\n-_-:"+j+"\tcount:"+false_count);
					
				}
				System.out.println("\nWith dynamiclly allocation according to real time bw:");
				System.out.println("index\tfalse_count\tused_slot_num\tused_bw\toccupied_bw");
				for(int j = 0;j<PacketService.TIME_BUCKET_NUM;j++)
				{
					System.out.println(j+"\t"+false_count_list[j]+"\t"+sum_used_slot_num_list[j]+"\t"+sum_bw_list[j]+"\t"+sum_occupied_bw_list[j]);
				}
*/

		//sm.judge_condition();
		//sm.lajihuishou();
		//sm.cirforward();

			
		
	}
	


}
