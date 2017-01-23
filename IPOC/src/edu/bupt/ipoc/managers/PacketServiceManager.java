package edu.bupt.ipoc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Pair;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.BandwidthTolerantPacketService;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;

public class PacketServiceManager{
	
	protected Map<Pair<Integer, Integer>, List<PacketService>> vertex_pair_ps_map = null;
	
	public BasicController bc;
	
	public PacketServiceManager(BasicController _bc)
	{
		vertex_pair_ps_map  = new HashMap<Pair<Integer,Integer>, List<PacketService>>();
		bc = _bc;
	}

	public boolean addService(PacketService ps) {
		
		//PacketService ps = (PacketService)ss;
		
		Pair<Integer,Integer> sd = new Pair<Integer,Integer>(ps.sourceNode,ps.destNode);
		
		if(vertex_pair_ps_map.get(sd) == null)
		{
			List<PacketService> psl = new ArrayList<PacketService>();
			psl.add(ps);
			vertex_pair_ps_map.put(sd, psl);
		}
		else
			vertex_pair_ps_map.get(sd).add(ps);
		return false;
	}

	public boolean deleteService(PacketService ss) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean clearAllServices() {
		for(List<PacketService> psl : vertex_pair_ps_map.values())
		{
			for(PacketService ps : psl)
			{
				ps.cleanMyselfButKeepBWStatistics();
			}
		}
		vertex_pair_ps_map.clear();
		return false;
	}
	
	public List<Service> shrinkedPSforMoreBW(int _source, int _dest, int differ_bw)
	{
		List<PacketService> _psl = vertex_pair_ps_map.get(new Pair<Integer,Integer>(_source, _dest));
		
		List<BandwidthTolerantPacketService> tem_psl = new ArrayList<BandwidthTolerantPacketService>();
		
		for(PacketService ps : _psl)
		{
			if(ps instanceof BandwidthTolerantPacketService)
			{
				tem_psl.add((BandwidthTolerantPacketService)ps);
			}
		}
		
		//sort
		int rest_bw = differ_bw;
		int tem_bw = 0;
		List<BandwidthTolerantPacketService> wait_btpsl = new ArrayList<BandwidthTolerantPacketService>();
		
		List<Pair<BandwidthTolerantPacketService,Integer>> wait_ps = new ArrayList<Pair<BandwidthTolerantPacketService,Integer>>();
		
		for(BandwidthTolerantPacketService btps : tem_psl)
		{
			tem_bw = btps.maxBWCanBeShrinked();
			if(tem_bw > 0)
			{
				if(tem_bw >= rest_bw)
					wait_ps.add(new Pair<BandwidthTolerantPacketService,Integer>(btps,rest_bw));
				else
					wait_ps.add(new Pair<BandwidthTolerantPacketService,Integer>(btps,tem_bw));
				rest_bw -= tem_bw;
			}
			if(rest_bw <= 0)
			{
				break;
			}
		}
		
		if(rest_bw <= 0)
		{
			List<Service> resutls = new ArrayList<Service>();
			for(Pair<BandwidthTolerantPacketService,Integer> pair : wait_ps)
			{
				List<Service> shrinked_vtl = pair.o1.shrinkedWithBW(pair.o2);
				resutls.addAll(shrinked_vtl);
			}
		}
		//else		
		return null;
	}
}
