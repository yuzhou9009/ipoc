package edu.bupt.ipoc.handler;

import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.control.YenTopKShortestPathsAlg;
import edu.asu.emit.qyan.alg.model.Pair;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.resource.OpticalLink;
import edu.bupt.ipoc.service.*;

public class OpticalResourceRequestHandler 
		implements RequestHanderInterface{
	
	public static final int BUILD = 0;
	public static final int DELE = 1;
	
	public static final int K_PATH_VALUE = 4;
	
	public static final int FIRST_FIT = 1;
	public static final int FF_FIT = 2;
	public static final int SB_FIT = 3;
	public static final int PRE_FIRST_FIT = 4;
	

	
	private VariableGraph graph_G;
	public BasicController bc;
	
	
	public OpticalResourceRequestHandler(VariableGraph _graph_G, BasicController _bc)
	{
		graph_G = _graph_G;
		//rs 资源已经在里面了 每条边上有对应的资源描述
		
		bc = _bc;
	}

	
	public void UpdateGraph(VariableGraph _graph_G)
	{
		graph_G = _graph_G;
	}

	private boolean ask_resource_for_traffic_FIRST(OpticalService tra_to_be_h)
	{	
		YenTopKShortestPathsAlg yenAlg1 = new YenTopKShortestPathsAlg(graph_G);
		
		yenAlg1.get_shortest_paths(
				graph_G.get_vertex(tra_to_be_h.sourceVertex), 
				graph_G.get_vertex(tra_to_be_h.sinkVertex),
				K_PATH_VALUE);//wait
		
		boolean is_there_resource = false;
		
	
		int[] path_slots; 
		for(int j = 0; j < yenAlg1.get_result_list().size();j++)
		{
			//System.out.println(""+yenAlg1.get_result_list().get(j) + "\t"+tra_to_be_h.rate);
			path_slots = get_slots_on_path(yenAlg1.get_result_list().get(j));

			//Pair<Integer,Double> estimate_result = new Pair<Integer,Double>(-1,-0.1);

			for(int x = 0;x < OpticalLink.SLOTS - tra_to_be_h.rate + 1 ;x++)
			{
				if(is_slots_continuty(path_slots,tra_to_be_h.rate,x))
				{
					is_there_resource = true;
					
					tra_to_be_h.path = yenAlg1.get_result_list().get(j);
					tra_to_be_h.start_slots = x;
					//System.out.println("start slots\t"+x+"\t"+tra_to_be_h.path);
					
					break;
				}
				
			}
			
			if(is_there_resource == true)
				break;
		}
		return is_there_resource;
	}
	
	private void release_resource_for_traffic(OpticalService tra_to_be_h)
	{
		release_resource(tra_to_be_h.path, tra_to_be_h.rate, tra_to_be_h.start_slots) ;
		
	}
	
	private void release_resource(Path path, int rate, int x) 
	{
		int[] _slots_tem = null;

		for(int i = 0;i<path.get_vertices().size() - 1;i++)
		{
			_slots_tem = graph_G.get_vertex_pair_weight_index().get(new Pair<Integer,Integer>(path.get_vertices().get(i).get_id(),path.get_vertices().get(i+1).get_id())).slots;
			for(int j = x;j< x+rate;j++)
				_slots_tem[j] = OpticalLink.FREE_FLAG;
			
			_slots_tem = graph_G.get_vertex_pair_weight_index().get(new Pair<Integer,Integer>(path.get_vertices().get(i+1).get_id(),path.get_vertices().get(i).get_id())).slots;
			for(int j = x;j< x+rate;j++)
				_slots_tem[j] = OpticalLink.FREE_FLAG;
		}
		
	}
	
	@Override
	public synchronized boolean handlerRequest(Service _traffic_to_be_handle,int requetType, Map<Integer,Constraint> constraints)//int diffirent_alg)
	{
		OpticalService traffic_to_be_handle = null;
		if(_traffic_to_be_handle instanceof OpticalService)
			traffic_to_be_handle = (OpticalService)_traffic_to_be_handle;
		else
		{
			System.out.println("The optical resource handler can only handler optical service request!");
			return false;
		}
		
		if(requetType == OpticalService.BUILD_REQUEST)// && diffirent_alg == FIRST_FIT )
		{
			if(ask_resource_for_traffic_FIRST(traffic_to_be_handle))
			{
				//System.out.println("There is resource"+traffic_to_be_handle.path);
				//System.out.println(""+traffic_to_be_handle.start_slots);
				set_resource(traffic_to_be_handle.path,traffic_to_be_handle.rate,traffic_to_be_handle.start_slots,OpticalLink.INIT_COLOR);
				//update_p_rate(traffic_to_be_handle.rate);
//				successNum++;
//				on_line_traffics.add(traffic_to_be_handle);
//				_timer.schedule(get_deletrafic_task(traffic_to_be_handle), traffic_to_be_handle.timeLong);
				return true;
			}
			else
			{
				//System.out.print("[F\tF\t]");
				return false;

			}
		}
/*		else if(build_or_dele == BUILD && diffirent_alg == FF_FIT)
		{
			if(ask_resource_for_traffic_FF(traffic_to_be_handle))
			{
				//System.out.println("There is resource"+traffic_to_be_handle.path);
				//System.out.println(""+traffic_to_be_handle.start_slots);
				set_resource(traffic_to_be_handle.path,traffic_to_be_handle.rate,traffic_to_be_handle.start_slots);
				update_p_rate(traffic_to_be_handle.rate);
				get_utilization_value();
				successNum++;
				on_line_traffics.add(traffic_to_be_handle);
				_timer.schedule(get_deletrafic_task(traffic_to_be_handle), traffic_to_be_handle.timeLong);
			}
			else
				;//System.out.println("FFFFFFFFFFFF");
				//_timer;
		}
		else if(build_or_dele == BUILD && diffirent_alg == PRE_FIRST_FIT )
		{
			if(ask_resource_for_traffic_PRE_FIRST(traffic_to_be_handle))
			{
				//System.out.println("There is resource"+traffic_to_be_handle.path);
				//System.out.println(""+traffic_to_be_handle.start_slots);
				set_resource(traffic_to_be_handle.path,traffic_to_be_handle.rate,traffic_to_be_handle.start_slots);
				//update_p_rate(traffic_to_be_handle.rate);
				successNum++;
				on_line_traffics.add(traffic_to_be_handle);
				_timer.schedule(get_deletrafic_task(traffic_to_be_handle), traffic_to_be_handle.timeLong);
			}
			else
				;//System.out.println("FFFFFFFFFFFF");
			
		}	
		else if(build_or_dele == BUILD && diffirent_alg == SB_FIT )
		{
			if(ask_resource_for_traffic_SB(traffic_to_be_handle))
			{
				//System.out.println("There is resource"+traffic_to_be_handle.path);
				//System.out.println(""+traffic_to_be_handle.start_slots);
				set_resource(traffic_to_be_handle.path,traffic_to_be_handle.rate,traffic_to_be_handle.start_slots);
				update_p_rate(traffic_to_be_handle.rate);
				successNum++;
				on_line_traffics.add(traffic_to_be_handle);
				_timer.schedule(get_deletrafic_task(traffic_to_be_handle), traffic_to_be_handle.timeLong);
			}
			else
				;//System.out.println("FFFFFFFFFFFF");
			
		}
*/
		else if(requetType == OpticalService.DELETE_RELEASE)
		{
//			on_line_traffics.remove(traffic_to_be_handle);
			release_resource_for_traffic(traffic_to_be_handle);
			return true;
			//System.out.println("Traffic "+traffic_to_be_handle.id+" 删路       "+ new Date());
			//System.out.println("Network state is :"+get_estimate_value());
		}
		return false;	
		
	}
		
	private int[] get_slots_on_path(Path path)
	{
		int[] _slots = null;
		int[] _slots_tem = null;
		_slots_tem = graph_G.get_vertex_pair_weight_index().get(new Pair<Integer,Integer>(path.get_vertices().get(0).get_id(),path.get_vertices().get(1).get_id())).slots;
		_slots=_slots_tem.clone();

		for(int i = 1;i<path.get_vertices().size() - 1;i++)
		{
			_slots_tem = graph_G.get_vertex_pair_weight_index().get(new Pair<Integer,Integer>(path.get_vertices().get(i).get_id(),path.get_vertices().get(i+1).get_id())).slots;
			make_and_with_two_slots(_slots,_slots_tem);//path.get(0);
		}
		
		return _slots;
	}
	
	private int[] get_slots_between_two_point(int _sourceVertex, int _sinkVertex)
	{
		int[] _slots_tem = graph_G.get_vertex_pair_weight_index().get(new Pair<Integer,Integer>(_sourceVertex,_sinkVertex)).slots.clone();
		return _slots_tem;
	}
	
	private void make_and_with_two_slots(int[] _slots, int[] _slots_tem) 
	{
		for(int i = 0;i<OpticalLink.SLOTS;i++)
			_slots[i] = _slots[i] | _slots_tem[i];
		
	}
	
	private boolean is_slots_continuty(int[] path_slots, int rate,int slots_position) 
	{
		for(int i = slots_position;i<slots_position + rate;i++)
			if(path_slots[i] != OpticalLink.FREE_FLAG)// == false)
				return false;
		return true;		
	}
	
	private void set_resource(Path _path, int rate, int x,int _colour) 
	{
		int[] _slots_tem = null;

		for(int i = 0;i<_path.get_vertices().size() - 1;i++)
		{
			_slots_tem = graph_G.get_vertex_pair_weight_index().get(new Pair<Integer,Integer>(_path.get_vertices().get(i).get_id(),_path.get_vertices().get(i+1).get_id())).slots;
			for(int j = x;j< x+rate;j++)
			{
				if(_slots_tem[j] != OpticalLink.FREE_FLAG)
					System.out.println("!!!!!!!!!error");
				_slots_tem[j] = _colour;
			}
			_slots_tem = graph_G.get_vertex_pair_weight_index().get(new Pair<Integer,Integer>(_path.get_vertices().get(i+1).get_id(),_path.get_vertices().get(i).get_id())).slots;
			for(int j = x;j< x+rate;j++)
			{
				if(_slots_tem[j] != OpticalLink.FREE_FLAG)
					System.out.println("!!!!!!!!!error");
				_slots_tem[j] = _colour;
			}
		}
	}



}
