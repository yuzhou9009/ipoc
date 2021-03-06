package edu.bupt.ipoc.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Pair;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.BandwidthTolerantPacketService;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.SubBTService;

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

	public boolean deleteService(PacketService ss) 
	{
		List<PacketService> psl = vertex_pair_ps_map.get(new Pair<Integer,Integer>(ss.sourceNode, ss.destNode));
		
		if(psl != null && psl.size() > 0)
			return psl.remove(ss);
		System.out.println("Wrong no existing packetservice");
		return true;
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
	
	public List<Pair<Service, Integer>> shrinkedPSforMoreBW(int _source, int _dest, int differ_bw)
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
		
		Collections.sort(tem_psl);
		Collections.reverse(tem_psl);
		
		int rest_bw = differ_bw;
		int tem_bw = 0;

		List<Pair<Service,Integer>> wait_ps = new ArrayList<Pair<Service,Integer>>();
		
		for(BandwidthTolerantPacketService btps : tem_psl)
		{
			tem_bw = btps.maxBWCanBeShrinked();
			if(tem_bw > 0)
			{
				if(tem_bw >= rest_bw)
					wait_ps.add(new Pair<Service, Integer>(btps,rest_bw));
				else
					wait_ps.add(new Pair<Service, Integer>(btps,tem_bw));
				rest_bw -= tem_bw;
			}
			if(rest_bw <= 0)
			{
				break;
			}
		}
		
		if(rest_bw <= 0)
		{
			List<Pair<Service,Integer>> results = new ArrayList<Pair<Service,Integer>>();
			
			List<Pair<Service,Integer>> shringked_vtls;
			
			BandwidthTolerantPacketService tem;
			for(Pair<Service,Integer> pair : wait_ps)
			{
				tem = (BandwidthTolerantPacketService)(pair.o1);
				shringked_vtls = tem.shrinkedWithBW(pair.o2);
				if(shringked_vtls != null)
					results.addAll(shringked_vtls);
				else
					System.out.println("Big mistake");
			}
			
			return results;
		}
		//else		
		return null;
	}

	private void combineServicesWithBw(List<Pair<Service, Integer>> results,
			List<Pair<Service, Integer>> shringked_vtls) {
		// TODO Auto-generated method stub
		//NOT necessary		
	}

	public void updateBTServicesStatue() {
		List<PacketService> remove_ps_list = new ArrayList<PacketService>();
		
		for(List<PacketService> _psl : vertex_pair_ps_map.values())
		{
			for(PacketService ps : _psl)
			{
				if(ps instanceof BandwidthTolerantPacketService)
				{
					if(((BandwidthTolerantPacketService)ps).checkStatue() == Service.BTPS_NEED_TO_BE_REMOVED)
					{
						remove_ps_list.add(ps);
					}					
				}
			}
		}
		if(remove_ps_list.size() > 0)
		{
			for(PacketService ps : remove_ps_list)
			{
				bc.handleServiceRequest(ps, Service.PS_REMOVED_REQUEST, null);
			}
		}
	}
	
/*	public int getAllBwPacketServiceOccupied()
	{
		int tem = 0;
		for(List<PacketService> _psl : this.vertex_pair_ps_map.values())
		{
			for(PacketService ps: _psl)
				tem += ps.getActualBwItUsed();
		}
		return tem;
	}
*/
	public List<PacketService> getAllPSs() {
		List<PacketService> all_ps = new ArrayList<PacketService>();
		for(List<PacketService> _psl : this.vertex_pair_ps_map.values())
		{
			all_ps.addAll(_psl);
		}
		return all_ps;
	}
	
	public List<PacketService> getAllPSsWithoutLowPriority() {
		List<PacketService> all_ps = new ArrayList<PacketService>();
		for(List<PacketService> _psl : this.vertex_pair_ps_map.values())
		{
			for(PacketService _ps : _psl)
				if(_ps.priority != Service.PRIORITY_LOW)
					all_ps.add(_ps);
		}
		return all_ps;
	}

	public List<PacketService> getPSs(int _source, int _dest) {
		
		return this.vertex_pair_ps_map.get(new Pair<Integer,Integer>(_source, _dest));
	}
}
