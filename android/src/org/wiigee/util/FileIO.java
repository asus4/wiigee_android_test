/*
 * wiigee - accelerometerbased gesture recognition
 * Copyright (C) 2007, 2008, 2009 Benjamin Poppinga
 * 
 * Developed at University of Oldenburg
 * Contact: wiigee@benjaminpoppinga.de
 *
 * This file is part of wiigee.
 *
 * wiigee is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.wiigee.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.wiigee.logic.GestureModel;
import org.wiigee.logic.HMM;
import org.wiigee.logic.Quantizer;

/**
 * This is a static class to support saving and loading complete gestures. I've
 * choosen not to use some kind of XML, because the big multidimensional arrays
 * would cause a huge senseless amount of xml-data. So KISS: Keep It Simple,
 * Stupid! ;) Comma-separated-vectors with some control data.
 * 
 * @author Benjamin 'BePo' Poppinga
 * 
 */
public class FileIO {

	public static void writeToFile(GestureModel m, String name) {
		try {
			// initialize file and get values
			BufferedWriter out = new BufferedWriter(new FileWriter(name+".txt"));
			int numStates = m.getNumStates();
			int numObservations = m.getNumObservations();
			double defaultProbability = m.getDefaultProbability();
			Quantizer quantizer = m.getQuantizer();
			HMM hmm = m.getHMM();
			
			out.write("# numStates:");
			out.newLine();
			out.write(Integer.toString(numStates));
			out.newLine();
			
			out.write("# numObservations:");
			out.newLine();
			out.write(Integer.toString(numObservations));
			out.newLine();
			
			out.write("# defaultProbability:");
			out.newLine();
			out.write(Double.toString(defaultProbability));
			out.newLine();
			
			out.write("# Quantizer: Radius");
			out.newLine();
			out.write(Double.toString(quantizer.getRadius()));
			out.newLine();
			out.write("# Quantizer: MAP");
			out.newLine();
			double[][] map = quantizer.getHashMap();
			for(int v=0; v<map.length; v++) {
				double[] d = map[v];
				out.write(Double.toString(d[0])+", "+Double.toString(d[1])+", "+Double.toString(d[2]));
				out.newLine();
			}
			
			out.write("# HMM: PI");
			out.newLine();
			double[] pi = hmm.getPi();
			for (int i=0; i<numStates; i++) {
				if(i==numStates-1) {
					out.write(Double.toString(pi[i]));
					out.newLine();
				} else {
					out.write(Double.toString(pi[i])+", ");
				}
			}
			
			out.write("# HMM: A");
			out.newLine();
			double[][] a = hmm.getA();
			for(int i=0; i<numStates; i++) {
				for(int j=0; j<numStates; j++) {
					if(j==numStates-1) {
						out.write(Double.toString(a[i][j]));
						out.newLine();
					} else {
						out.write(Double.toString(a[i][j])+", ");
					}
				}
			}
			
			out.write("# HMM: B");
			out.newLine();
			double[][] b = hmm.getB();
			for(int i=0; i<numStates; i++) {
				for(int j=0; j<numObservations; j++) {
					if(j==numObservations-1) {
						out.write(Double.toString(b[i][j]));
						out.newLine();
					} else {
						out.write(Double.toString(b[i][j])+", ");
					}
				}
			}
			
			out.write("# END");
			
			// close file
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println("Error: Write to File!");
			e.printStackTrace();
		}
	}

	public static GestureModel readFromFile(String name) {
		try {
			// initialize file and create values
			BufferedReader in = new BufferedReader(new FileReader(name+".txt"));
			int numStates = 0;
			int numObservations = 0;
			double defaultprobability = 0;
			double radius = 0;
			double[][] map = new double[numObservations][3];
			double[] pi = new double[numStates];
			double[][] a = new double[numStates][numStates];
			double[][] b = new double[numStates][numObservations];
			
			String line;
			int position = 0;
			while(in.ready()) {
				line = in.readLine();
				if(!line.startsWith("#")) { // isn't a comment
					switch (position++) {
					case 0:
						numStates = Integer.parseInt(line);
						break;
					case 1:
						numObservations = Integer.parseInt(line);
						break;
					case 2:
						defaultprobability = Double.parseDouble(line);
						break;
					case 3:
						radius = Double.parseDouble(line);
						break;
					case 4:
						map = new double[numObservations][3];
						for(int i=0; i<numObservations; i++) {
							String s[] = line.split(", ");
							double[] d = new double[] { Double.parseDouble(s[0]),
														Double.parseDouble(s[1]),
														Double.parseDouble(s[2]) };
							map[i] = d;
							line = in.ready() ? in.readLine() : "";
						}
						break;
					case 5:
						pi = new double[numStates];
						String pi_row[] = line.split(", ");
						for(int i=0; i<numStates; i++) {
							pi[i] = Double.parseDouble(pi_row[i]);
						}
						break;
					case 6:
						a = new double[numStates][numStates];
						for(int i=0; i<numStates; i++) {
							String a_row[] = line.split(", ");
							for(int j=0; j<numStates; j++) {
								a[i][j] = Double.parseDouble(a_row[j]);
							}
							line = in.ready() ? in.readLine() : "";
						}
						break;
					case 7:
						b = new double[numStates][numObservations];
						for(int i=0; i<numStates; i++) {
							String b_row[] = line.split(", ");
							for(int j=0; j<numObservations; j++) {
								b[i][j] = Double.parseDouble(b_row[j]);
							}
							line = in.ready() ? in.readLine() : "";
						}
						break;
					default:
						System.out.println("SWITCH EMPTY!");
						break;
					}
				}
			}
			
			GestureModel ret = new GestureModel();
			ret.setDefaultProbability(defaultprobability);
			
			Quantizer quantizer = new Quantizer(numStates);
			quantizer.setUpManually(map, radius);
			ret.setQuantizer(quantizer);
			
			HMM hmm = new HMM(numStates, numObservations);
			hmm.setPi(pi);
			hmm.setA(a);
			hmm.setB(b);
			ret.setHMM(hmm);
			
			return ret;
		} catch (Exception e) {
			System.out.println("Error: Read from File!");
			e.printStackTrace();
		}
		return null; // bad error case
	}

}
