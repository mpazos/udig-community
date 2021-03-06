/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package es.axios.udig.spatialoperations.ui.taskmanager;

/**
 * Generates the next sequence number
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila
 *
 */
final class Sequencer {
	
	private static Sequencer THIS = new Sequencer();
	private int last = 0;
	
	private Sequencer(){
		// singleton pattern
	}
	
	public synchronized static  Sequencer getInstance(){
	
		return THIS;
	}

	public synchronized int next() {
		
		return new Integer(++last);
	}
	
	
	

}
