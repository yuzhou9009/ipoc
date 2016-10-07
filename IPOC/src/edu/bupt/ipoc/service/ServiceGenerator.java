package edu.bupt.ipoc.service;

import java.util.List;
import java.util.Vector;

import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.bupt.ipoc.managers.ServiceManager;
import edu.bupt.ipoc.service.*;

public class ServiceGenerator {
	
	//End to end service generator, the service can be packet service or packet service
	
	private VariableGraph graph_G;
	
	private static int id_tem = 0;
	
	public static final int SLEEP_BASE_NUM = 100;
	public static final int LASTTIME_BASE_NUM = 12000;
	
	public ServiceGenerator(VariableGraph _graph_G)
	{
		graph_G = _graph_G;
		//rs 资源已经在里面了 每条边上有对应的资源描述
	}
	
	public void updateGraph(VariableGraph _graph_G)
	{
		graph_G = _graph_G;
	}
	
	public PacketService create_PacketService(int _static_bw){
		
		int a,b,tem,_priority;

		a = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		b = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		while(a==b)
		{
			a = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
			b = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		}
		if(a > b)
		{
			tem = a;
			a = b;
			b= tem;
		}
		
		tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(1,100);
		if(tem<50)
			_priority = 1;
		else if(tem>=50 && tem <=90)
			_priority = 2;
		else
			_priority = 3;
		
		return new PacketService(generate_an_id(),a,b,_priority,_static_bw,0);
	}
	
	public PacketService create_PacketService(int _sourceVertex, int _sinkVertex, int _static_bw){
		
		int tem,_priority;

		if(_sourceVertex == _sinkVertex)
		{
			System.out.println("With same source and sink address in OpticalService.java");
		}
		else if(_sourceVertex > _sinkVertex)
		{
			tem = _sourceVertex;
			_sourceVertex = _sinkVertex;
			_sinkVertex= tem;
		}
		
		tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(1,100);
		if(tem<60)
			_priority = 1;
		else if(tem>=60 && tem <=85)
			_priority = 2;
		else
			_priority = 3;
		
		return new PacketService(generate_an_id(),_sourceVertex,_sinkVertex,_priority, _static_bw,0);
	}
	
	public List<PacketService> random_PacketServices(int random_num,int _static_bw,boolean last_forever_f){
		//
		List<PacketService> ps_list = new Vector<PacketService>();
		int a,b,tem,_priority;
		for(int i = 0; i < random_num; i++)
		{
			a = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
			b = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
			while(a==b)
			{
				a = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
				b = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
			}
			if(a > b)
			{
				tem = a;
				a = b;
				b= tem;
			}
			
			tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(1,100);
			if(tem<40)
				_priority = 1;
			else if(tem>=40 && tem <=80)
				_priority = 2;
			else
				_priority = 3;
			
			if(PacketService.INTERACTIVE == last_forever_f)
				ps_list.add(new PacketService(generate_an_id(),a,b,_priority,_static_bw,0));
			else
			{
				int timeLong_tem = (int)(P_rand(5)+1)*LASTTIME_BASE_NUM;
				ps_list.add(new PacketService(generate_an_id(),a,b,_priority,_static_bw,timeLong_tem));
			}
		}
		return ps_list;		
	}
	
	public OpticalService create_OpticalService(){
		
		int a,b,tem;

		a = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		b = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		while(a==b)
		{
			a = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
			b = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		}
		if(a > b)
		{
			tem = a;
			a = b;
			b= tem;
		}
		return new OpticalService(generate_an_id(),a,b,OpticalService.SINGLE_SLOT,OpticalService.INIT_TIME_LONG);
	}
	
	public OpticalService create_OpticalService(int _sourceVertex, int _sinkVertex){
		
		int tem;

		if(_sourceVertex == _sinkVertex)
		{
			System.out.println("With same source and sink address in OpticalService.java");
		}
		else if(_sourceVertex > _sinkVertex)
		{
			tem = _sourceVertex;
			_sourceVertex = _sinkVertex;
			_sinkVertex= tem;
		}
		return new OpticalService(generate_an_id(),_sourceVertex,_sinkVertex,OpticalService.SINGLE_SLOT,OpticalService.INIT_TIME_LONG);
	}
	
	public static double P_rand(double Lamda)
	{// 泊松分布
		double x=0,b=1,c=Math.exp(-Lamda),u; 
		do {
			u=Math.random();
			b *=u;
			if(b>=c)
				x++;
		}while(b>=c);
		return x;
	}
	
	public static int generate_an_id()
	{
		return id_tem++;
	}
	
	/*
	 * 
	 * */
	

}
