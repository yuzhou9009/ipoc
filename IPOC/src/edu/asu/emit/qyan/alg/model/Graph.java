/*
 *
 * Copyright (c) 2004-2008 Arizona State University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ARIZONA STATE UNIVERSITY ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL ARIZONA STATE UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package edu.asu.emit.qyan.alg.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

//import edu.bupt.rsa.George;
import edu.asu.emit.qyan.alg.model.abstracts.BaseGraph;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;
import edu.bupt.ipoc.resource.OpticalLink;

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 783 $
 * @latest $Date: 2009-06-19 12:19:27 -0700 (Fri, 19 Jun 2009) $
 */
public class Graph implements BaseGraph
{
	public final static double DISCONNECTED = Double.MAX_VALUE;
	
	// index of fan-outs of one vertex
	protected Map<Integer, Set<BaseVertex>> _fanout_vertices_index =
		new HashMap<Integer, Set<BaseVertex>>();
	
	

	// index for fan-ins of one vertex
	protected Map<Integer, Set<BaseVertex>> _fanin_vertices_index =
		new HashMap<Integer, Set<BaseVertex>>();
	
	// index for edge weights in the graph
	protected Map<Pair<Integer, Integer>, OpticalLink> _vertex_pair_weight_index = 
		new HashMap<Pair<Integer,Integer>, OpticalLink>();
	
	// index for vertices in the graph
	protected Map<Integer, BaseVertex> _id_vertex_index = 
		new HashMap<Integer, BaseVertex>();
	
	// list of vertices in the graph 
	protected List<BaseVertex> _vertex_list = new Vector<BaseVertex>();
	
	// the number of vertices in the graph
	protected int _vertex_num = 0;
	
	// the number of arcs in the graph
	protected int _edge_num = 0;
	
	protected List<Pair<Integer, Integer>> _pair_list = new Vector<Pair<Integer, Integer>>();
	//protected List<Resource> _rs_list = new Vector<Resource>();
	
	/**
	 * Constructor 1 
	 * @param data_file_name
	 */
	public Graph(final String data_file_name)
	{
		import_from_file(data_file_name);
	}
	
	/**
	 * Constructor 2
	 * 
	 * @param graph
	 */
	public Graph(final Graph graph_)
	{
		_vertex_num = graph_._vertex_num;
		_edge_num = graph_._edge_num;
		_vertex_list.addAll(graph_._vertex_list);
		_id_vertex_index.putAll(graph_._id_vertex_index);
		_fanin_vertices_index.putAll(graph_._fanin_vertices_index);
		_fanout_vertices_index.putAll(graph_._fanout_vertices_index);
		_vertex_pair_weight_index.putAll(graph_._vertex_pair_weight_index);
	}
	
	/**
	 * Default constructor 
	 */
	public Graph(){};
	
	/**
	 * Clear members of the graph.
	 */
	public void clear()
	{
		Vertex.reset();
		_vertex_num = 0;
		_edge_num = 0; 
		_vertex_list.clear();
		_id_vertex_index.clear();
		_fanin_vertices_index.clear();
		_fanout_vertices_index.clear();
		_vertex_pair_weight_index.clear();
	}
	
	/**
	 * There is a requirement for the input graph. 
	 * The ids of vertices must be consecutive. 
	 *  
	 * @param data_file_name
	 */
	public void import_from_file(final String data_file_name)
	{
		// 0. Clear the variables 
		clear();
		
		try
		{
			// 1. read the file and put the content in the buffer
			FileReader input = new FileReader(data_file_name);
			BufferedReader bufRead = new BufferedReader(input);

			boolean is_first_line = true;
			String line; 	// String that holds current file line
			
			// 2. Read first line
			line = bufRead.readLine();
			while(line != null)
			{
				// 2.1 skip the empty line
				if(line.trim().equals("")) 
				{
					line = bufRead.readLine();
					continue;
				}
				
				// 2.2 generate nodes and edges for the graph
				if(is_first_line)
				{
					//2.2.1 obtain the number of nodes in the graph 
					
					is_first_line = false;
					_vertex_num = Integer.parseInt(line.trim());
					for(int i=0; i<_vertex_num; ++i)
					{
						BaseVertex vertex = new Vertex();
						_vertex_list.add(vertex);
						_id_vertex_index.put(vertex.get_id(), vertex);
					}
					
				}else
				{
					//2.2.2 find a new edge and put it in the graph  
					String[] str_list = line.trim().split("\\s");
					
					int start_vertex_id = Integer.parseInt(str_list[0]);
					int end_vertex_id = Integer.parseInt(str_list[1]);
					double weight = Double.parseDouble(str_list[2]);
					add_edge(start_vertex_id, end_vertex_id, weight);
					
					add_edge(end_vertex_id, start_vertex_id, weight);//george
					_pair_list.add(new Pair(start_vertex_id,end_vertex_id));
				}
				//
				line = bufRead.readLine();
			}
			bufRead.close();

		}catch (IOException e)
		{
			// If another exception is generated, print a stack trace
			e.printStackTrace();
		}
	}

	/**
	 * Note that this may not be used externally, because some other members in the class
	 * should be updated at the same time. 
	 * 
	 * @param start_vertex_id
	 * @param end_vertex_id
	 * @param weight
	 */
	public void add_edge(int start_vertex_id, int end_vertex_id, double weight)
	//protected void add_edge(int start_vertex_id, int end_vertex_id, double weight)
	{
		// actually, we should make sure all vertices ids must be correct. 
		if(!_id_vertex_index.containsKey(start_vertex_id)
		|| !_id_vertex_index.containsKey(end_vertex_id) 
		|| start_vertex_id == end_vertex_id)
		{
			throw new IllegalArgumentException("The edge from "+start_vertex_id
					+" to "+end_vertex_id+" does not exist in the graph.");
		}
		
		// update the adjacent-list of the graph
		Set<BaseVertex> fanout_vertex_set = new HashSet<BaseVertex>();
		if(_fanout_vertices_index.containsKey(start_vertex_id))
		{
			fanout_vertex_set = _fanout_vertices_index.get(start_vertex_id);
		}
		fanout_vertex_set.add(_id_vertex_index.get(end_vertex_id));
		_fanout_vertices_index.put(start_vertex_id, fanout_vertex_set);
		
		//
		Set<BaseVertex> fanin_vertex_set = new HashSet<BaseVertex>();
		if(_fanin_vertices_index.containsKey(end_vertex_id))
		{
			fanin_vertex_set = _fanin_vertices_index.get(end_vertex_id);
		}
		fanin_vertex_set.add(_id_vertex_index.get(start_vertex_id));
		_fanin_vertices_index.put(end_vertex_id, fanin_vertex_set);

		// store the new edge 
		OpticalLink ol = new OpticalLink();
		ol.weight = weight;
		ol.setStart_index(start_vertex_id);
		ol.setEnd_index(end_vertex_id);
		
		//_rs_list.add(rs);
		
		_vertex_pair_weight_index.put(
				new Pair<Integer, Integer>(start_vertex_id, end_vertex_id), 
				ol);
		
		++_edge_num;
	}
	
	/*George*/
	public void add_edge(int start_vertex_id, int end_vertex_id, OpticalLink ol)
	{
		// actually, we should make sure all vertices ids must be correct. 
		if(!_id_vertex_index.containsKey(start_vertex_id)
		|| !_id_vertex_index.containsKey(end_vertex_id) 
		|| start_vertex_id == end_vertex_id)
		{
			throw new IllegalArgumentException("The edge from "+start_vertex_id
					+" to "+end_vertex_id+" does not exist in the graph.");
		}
		
		// update the adjacent-list of the graph
		Set<BaseVertex> fanout_vertex_set = new HashSet<BaseVertex>();
		if(_fanout_vertices_index.containsKey(start_vertex_id))
		{
			fanout_vertex_set = _fanout_vertices_index.get(start_vertex_id);
		}
		fanout_vertex_set.add(_id_vertex_index.get(end_vertex_id));
		_fanout_vertices_index.put(start_vertex_id, fanout_vertex_set);
		
		//
		Set<BaseVertex> fanin_vertex_set = new HashSet<BaseVertex>();
		if(_fanin_vertices_index.containsKey(end_vertex_id))
		{
			fanin_vertex_set = _fanin_vertices_index.get(end_vertex_id);
		}
		fanin_vertex_set.add(_id_vertex_index.get(start_vertex_id));
		_fanin_vertices_index.put(end_vertex_id, fanin_vertex_set);

		// store the new edge 
		//Resource rs = new Resource();
		//rs.weight = weight;
		_vertex_pair_weight_index.put(
				new Pair<Integer, Integer>(start_vertex_id, end_vertex_id), 
				ol);
		
		++_edge_num;
	}
	
	/**
	 * Store the graph information into a file. 
	 * 
	 * @param file_name
	 */
	public void export_to_file(final String file_name)
	{
		//1. prepare the text to export
		StringBuffer sb = new StringBuffer();
		sb.append(_vertex_num+"\n\n");
		for(Pair<Integer, Integer> cur_edge_pair : _vertex_pair_weight_index.keySet())
		{
			int starting_pt_id = cur_edge_pair.first();
			int ending_pt_id = cur_edge_pair.second();
			double weight = _vertex_pair_weight_index.get(cur_edge_pair).weight;
			sb.append(starting_pt_id+"	"+ending_pt_id+"	"+weight+"\n");
		}
		//2. open the file and put the data into the file. 
		Writer output = null;
		try {
			// use buffering
			// FileWriter always assumes default encoding is OK!
			output = new BufferedWriter(new FileWriter(new File(file_name)));
			output.write(sb.toString());
		}catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}catch(IOException e)
		{
			e.printStackTrace();
		}finally {
			// flush and close both "output" and its underlying FileWriter
			try
			{
				if (output != null) output.close();
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.asu.emit.qyan.alg.model.abstracts.BaseGraph#get_adjacent_vertices(edu.asu.emit.qyan.alg.model.abstracts.BaseVertex)
	 */
	public Set<BaseVertex> get_adjacent_vertices(BaseVertex vertex)
	{
		return _fanout_vertices_index.containsKey(vertex.get_id()) 
				? _fanout_vertices_index.get(vertex.get_id()) 
				: new HashSet<BaseVertex>();
	}

	/* (non-Javadoc)
	 * @see edu.asu.emit.qyan.alg.model.abstracts.BaseGraph#get_precedent_vertices(edu.asu.emit.qyan.alg.model.abstracts.BaseVertex)
	 */
	public Set<BaseVertex> get_precedent_vertices(BaseVertex vertex)
	{
		return _fanin_vertices_index.containsKey(vertex.get_id()) 
				? _fanin_vertices_index.get(vertex.get_id()) 
				: new HashSet<BaseVertex>();
	}
	
	/* (non-Javadoc)
	 * @see edu.asu.emit.qyan.alg.model.abstracts.BaseGraph#get_edge_weight(edu.asu.emit.qyan.alg.model.abstracts.BaseVertex, edu.asu.emit.qyan.alg.model.abstracts.BaseVertex)
	 */
	public double get_edge_weight(BaseVertex source, BaseVertex sink)
	{
		return _vertex_pair_weight_index.containsKey(
					new Pair<Integer, Integer>(source.get_id(), sink.get_id())) ? 
							_vertex_pair_weight_index.get(
									new Pair<Integer, Integer>(source.get_id(), sink.get_id())).weight 
						  : DISCONNECTED;
	}

	/**
	 * Set the number of vertices in the graph
	 * @param num
	 */
	public void set_vertex_num(int num)
	{
		_vertex_num = num;
	}
	
	public int get_vertex_num()
	{
		return _vertex_num;
	}//george
	
	/**
	 * Return the vertex list in the graph.
	 */
	public List<BaseVertex> get_vertex_list()
	{
		return _vertex_list;
	}
	
	public List<BaseVertex> get_vertex_list(boolean a)
	{
		return _vertex_list;
	}//george
	
	/**
	 * Get the vertex with the input id.
	 * 
	 * @param id
	 * @return
	 */
	public BaseVertex get_vertex(int id)
	{
		return _id_vertex_index.get(id);
	}
	
	/*public Map<Pair<Integer, Integer>, Resource> get__vertex_pair_weight_index()
	{
		return _vertex_pair_weight_index;
	}*///george
	
	public List<Pair<Integer, Integer>> get_pair_list()
	{
		return _pair_list;
	}
	
	public Map<Integer, BaseVertex> get_id_vertex_index()
	{
		return _id_vertex_index;
	}//george
	
	public boolean is_all_connection()
	{
		
		for(Pair<Integer, Integer> cur_edge_pair : _vertex_pair_weight_index.keySet())
		{
			int starting_pt_id = cur_edge_pair.first();
			int ending_pt_id = cur_edge_pair.second();
			double weight = _vertex_pair_weight_index.get(cur_edge_pair).weight;
			//sb.append(starting_pt_id+"	"+ending_pt_id+"	"+weight+"\n");
		}
		return true;
	}

	public Map<Pair<Integer, Integer>, OpticalLink> get_vertex_pair_weight_index() {
		return _vertex_pair_weight_index;
	}
	
	public void clear_all_resource()
	{
		for(int i = 0;i<_pair_list.size();i++)
		{
			Pair<Integer,Integer> _pair_tem = _pair_list.get(i);
			_vertex_pair_weight_index.get(new Pair<Integer,Integer>(_pair_tem.o1,_pair_tem.o2) ).clear_slots();
			_vertex_pair_weight_index.get(new Pair<Integer,Integer>(_pair_tem.o2,_pair_tem.o1) ).clear_slots();
		}
	}
	public Map<Integer, Set<BaseVertex>> get_fanout_vertices_index() {
		return _fanout_vertices_index;
	}
}
