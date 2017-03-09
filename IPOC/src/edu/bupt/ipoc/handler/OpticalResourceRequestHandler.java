package edu.bupt.ipoc.handler;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.asu.emit.qyan.alg.control.YenTopKShortestPathsAlg;
import edu.asu.emit.qyan.alg.model.Pair;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;
import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.resource.OpticalLink;
import edu.bupt.ipoc.service.*;

public class OpticalResourceRequestHandler{
	
	public static final int BUILD = 0;
	public static final int DELE = 1;
	
	public static final int K_PATH_VALUE = 4;
	
	public static final int FIRST_FIT = 1;
	public static final int FF_FIT = 2;
	public static final int SB_FIT = 3;
	public static final int PRE_FIRST_FIT = 4;
	

	
	private VariableGraph graph_G;
	public BasicController bc;
	
	private List<List<Pair<Integer, Integer>>> connect_list = new Vector<List<Pair<Integer, Integer>>>();	
	public static final int _rate_KIND = 1;
	private double[] p_rate = null;
	static int allnum = 0;
	
	public OpticalResourceRequestHandler(VariableGraph _graph_G, BasicController _bc)
	{
		graph_G = _graph_G;
		//rs 资源已经在里面了 每条边上有对应的资源描述
		
		bc = _bc;
		
		p_rate = new double[_rate_KIND];
		for(int j = 0;j <_rate_KIND ;j++)
			p_rate[j] = 1.0/_rate_KIND;
		
		get_set_of_links_of_nodes();
	}

	
	public void UpdateGraph(VariableGraph _graph_G)
	{
		graph_G = _graph_G;
	}

	public void get_set_of_links_of_nodes()
	{
		for(int i = 0;i<graph_G.get_vertex_num();i++)
		{
			List<Pair<Integer,Integer>> lst = new Vector<Pair<Integer, Integer>>();
			//;
			
			
			for(BaseVertex cur_adjacent_vertex : graph_G.get_fanout_vertices_index().get(i))
			{
				Pair<Integer,Integer> pair_tem = new Pair<Integer,Integer>(i,cur_adjacent_vertex.get_id());
				lst.add(pair_tem);
			}

			
			connect_list.add(lst);
			allnum++;
			
		}
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
	
	
	private boolean ask_resource_for_traffic_FF(OpticalService tra_to_be_h)
	{
		//System.out.println(""+new Date().getTime());
		VariableGraph new_graph = new VariableGraph();
		new_graph.get_vertex_list().addAll(graph_G.get_vertex_list());
		new_graph.get_id_vertex_index().putAll(graph_G.get_id_vertex_index());
		new_graph.set_vertex_num(graph_G.get_vertex_num());
		int vertex_pair_num = graph_G.get_pair_list().size()*2;
		//System.out.println(""+ new_graph.get_vertex_num()+"  "+vertex_pair_num +"\t"+ _graph.get_pair_list().size());
		OpticalLink rs_tem;
		for(int i = 0;i< graph_G.get_pair_list().size();i++)
		{
			rs_tem = graph_G.get_vertex_pair_weight_index().get(graph_G.get_pair_list().get(i));
			if(rs_tem.count_slots_num()>=tra_to_be_h.rate)
			{
				new_graph.add_edge(rs_tem.getStart_index(),
						rs_tem.getEnd_index(), 
						rs_tem);
				new_graph.add_edge(rs_tem.getEnd_index(), 
						rs_tem.getStart_index(),
						rs_tem);
			}
		}//新图生成好了
		
		//System.out.println("The new graph has "+new_graph.get_vertex_pair_weight_index().size() +"links");
		
		
		YenTopKShortestPathsAlg yenAlg1 = new YenTopKShortestPathsAlg(new_graph);
		
		List<Path> shortest_paths_list = yenAlg1.get_shortest_paths(
				new_graph.get_vertex(tra_to_be_h.sourceVertex), 
				new_graph.get_vertex(tra_to_be_h.sinkVertex),
				4);//wait
		//System.out.println(yenAlg1.get_result_list().size());
		//K条路算好了 下面应该要验证每条路上的是否有可用资源了
		
		boolean is_there_resource = false;
		
		List<Pair<Path,Pair<Integer,Double>>> paths_with_result = new Vector<Pair<Path,Pair<Integer,Double>>>();
		
		int[] path_slots; 
		for(int j = 0; j < yenAlg1.get_result_list().size();j++)
		{
			//System.out.println(""+yenAlg1.get_result_list().get(j) + "\t"+tra_to_be_h.rate);
			path_slots = get_slots_on_path(yenAlg1.get_result_list().get(j));

			Pair<Integer,Double> estimate_result = new Pair<Integer,Double>(-1,-0.1);

			for(int x = 0;x < OpticalLink.SLOTS - tra_to_be_h.rate + 1 ;x++)
			{
				if(is_slots_continuty(path_slots,tra_to_be_h.rate,x))
				{
					double dd = -0.1;
					set_resource(yenAlg1.get_result_list().get(j),tra_to_be_h.rate,x,OpticalLink.INIT_COLOR);
					dd  = get_estimate_value();
					//System.out.println(""+x+"\t"+tra_to_be_h.rate+"\t"+dd);
					compare_the_estimate_value(estimate_result,dd,x);
					is_there_resource = true;
					release_resource(yenAlg1.get_result_list().get(j),tra_to_be_h.rate,x);
					//break;
				}
				
			}//单条链路上的资源已经评估完,有可能路上是没有资源的
			
			if(estimate_result.o2 >=0)
			{
				estimate_result.o2 = estimate_result.o2/yenAlg1.get_result_list().get(j).get_weight();
				paths_with_result.add(new Pair<Path,Pair<Integer,Double>>(yenAlg1.get_result_list().get(j),estimate_result));
			}
			else if(estimate_result.o2 <0 && is_there_resource == true &&paths_with_result.size() == 0)
			{
				System.out.println("Let's see what happened");
			}
			//if(is_there_resource == true)
			//	break;
			/*
			path_slots = get_slots_on_path(yenAlg1.get_result_list().get(j));
			for(int pp =0;pp<Resource.SLOTS;pp++)
				if(path_slots[pp])
					System.out.print("1");
				else
					System.out.print("0");
			System.out.println();
			*/
		}//所有路上的结果都得到了，下一步在这里面选择一个结果
		
		
		if(paths_with_result.size() == 0)
		{
			if(is_there_resource)
				System.out.println("!!!!!!!!!!!Warning something is wrong");			
		}
		else if(paths_with_result.size() >= 1)
		{
			//System.out.println("HERE");
			Path path_tem = null;// paths_with_result.get(0).o1;
			int start_s_tem = -1;
			double _value = -0.1;
			for(int _tt = 0;_tt<paths_with_result.size();_tt++)
			{
				//System.out.println(""+_tt+"\t"+paths_with_result.get(_tt).o1+"\t"+
				//		paths_with_result.get(_tt).o2.o2+"\t"+
				//		paths_with_result.get(_tt).o2.o1);
				//path_tem = paths_with_result.get(_tt).o1;
				if(paths_with_result.get(_tt).o2.o2 > _value)
				{
					start_s_tem = paths_with_result.get(_tt).o2.o1;
					path_tem = paths_with_result.get(_tt).o1;
					_value = paths_with_result.get(_tt).o2.o2;
				}
				else if(paths_with_result.get(_tt).o2.o2 == _value)
				{
					if( Math.abs(paths_with_result.get(_tt).o2.o1-(OpticalLink.SLOTS/2)) < Math.abs(start_s_tem-(OpticalLink.SLOTS/2)))//策略wait
					{
						start_s_tem = paths_with_result.get(_tt).o2.o1;
						path_tem = paths_with_result.get(_tt).o1;
						
					}
				}
			}
			tra_to_be_h.path = path_tem;
			tra_to_be_h.start_slots = start_s_tem;
			System.out.print(_value * path_tem.get_weight() + "\t");
			
		}
		
		//System.out.println("start slots\t"+tra_to_be_h.start_slots+"\t"+tra_to_be_h.path);
		//System.out.println(""+new Date().getTime());
		
		// TODO Auto-generated method stub
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
	
	public synchronized boolean handlerRequest(OpticalService traffic_to_be_handle,int requetType, Map<Integer,Constraint> constraints)//int diffirent_alg)
	{		
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
		/*else if(requetType == OpticalService.BUILD_REQUEST)// && diffirent_alg == FF_FIT)
		{
			if(ask_resource_for_traffic_FF(traffic_to_be_handle))
			{
				//System.out.println("There is resource"+traffic_to_be_handle.path);
				//System.out.println(""+traffic_to_be_handle.start_slots);
				set_resource(traffic_to_be_handle.path,traffic_to_be_handle.rate,traffic_to_be_handle.start_slots,OpticalLink.INIT_COLOR);
//				update_p_rate(traffic_to_be_handle.rate);
//				get_utilization_value();
//				successNum++;
//				on_line_traffics.add(traffic_to_be_handle);
//				_timer.schedule(get_deletrafic_task(traffic_to_be_handle), traffic_to_be_handle.timeLong);
				return true;
			}
			else
			{
				System.out.print("[F\tF\t]");
				return false;

			}
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

	public Double get_estimate_value() 
	{
		double _estimate_value = 0;
		//private List<List<Pair<Integer, Integer>>> connect_list = new Vector<List<Pair<Integer, Integer>>>();
		List<List<Pair<Integer, Integer>>> _connect_list = getConnect_list();
		int[] _slots;
		for(int i = 0;i < _connect_list.size();i++)
		{			
			_slots = get_slots_on_links(_connect_list.get(i));
			_estimate_value += estimate_slots(_slots);
			
			/*
			double d_tem = 0.0;
			for(int p = 0;p<_connect_list.get(i).size();p++)
			{
				d_tem += Math.pow(((estimate_slots(_graph.get_vertex_pair_weight_index().get(_connect_list.get(i).get(p)).slots))
							-estimate_slots(_slots)),2);
				
			}
			d_tem = 1 - Math.sqrt(d_tem/_connect_list.get(i).size());
			_estimate_value += d_tem;
			*/
			//Math.pow(arg0, 2);
			//for(int j = 0 j <_connect_list.)
		}
		_estimate_value = _estimate_value/_connect_list.size();
		//System.out.println("estimate_value "+_estimate_value);
		return _estimate_value;
		
	}

	private int[] get_slots_on_links(List<Pair<Integer, Integer>> list) 
	{
		int[] _slots = new int[OpticalLink.SLOTS];
		for(int i = 0;i<OpticalLink.SLOTS;i++)
			_slots[i] = OpticalLink.FREE_FLAG;
		
		for(int j = 0;j < list.size();j++)
		{
			make_and_with_two_slots(_slots,graph_G.get_vertex_pair_weight_index().get(list.get(j)).slots);
		}
		return _slots;
	}	
	
	private void compare_the_estimate_value(
			Pair<Integer, Double> estimate_result, double dd, int x) 
	{
		if(dd > estimate_result.o2)
		{
			estimate_result.o2 = dd;
			estimate_result.o1 = x;
		}
		else if(dd == estimate_result.o2)
		{
			if(estimate_result.o1 == -1)
				estimate_result.o1 =  x;
			else if(Math.abs(estimate_result.o1 - (OpticalLink.SLOTS/2)) < Math.abs(x - (OpticalLink.SLOTS/2)))
				estimate_result.o1 = x;
		}
	}
	
	private double estimate_slots(int[] _slots) 
	{
		double _value = 0.0;
		for(int i = 0;i<_rate_KIND;i++)
		{
			_value += (get_possible_of_Q(_slots,i+1) * p_rate[i]);
		}
		//System.out.println("value "+_value);
		return _value;
	}
	
	public double get_possible_of_Q(int[] _slots, int _rate) 
	{
		int _count = 0;
		//int _empty_slots  = 0;
		int _tem;
		for(int i = 0;i<OpticalLink.SLOTS - _rate + 1;i++)
		{
			_tem = OpticalLink.FREE_FLAG;
			for(int j = i;j< i + _rate;j++)
			{
				_tem = _tem | _slots[j];
			}//wait算法可提高
			if(_tem == OpticalLink.FREE_FLAG)
				_count++;
		}
		//for(int k = 0;k<OpticalLink.SLOTS;k++)
		//	if(_slots[k] == true)
		//		_empty_slots++;
		
		return (double)_count/(OpticalLink.SLOTS - _rate + 1);
		//if(_empty_slots - _rate + 1 ==0)
		//	return 0;
		//return (double)_count/(_empty_slots - _rate + 1);
	}
	
	public List<List<Pair<Integer, Integer>>> getConnect_list() {
		return connect_list;
	}
}
