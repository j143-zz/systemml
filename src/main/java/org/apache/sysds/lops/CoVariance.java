/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysds.lops;

import org.apache.sysds.lops.LopProperties.ExecType;
import org.apache.sysds.common.Types.DataType;
import org.apache.sysds.common.Types.ValueType;

/**
 * Lop to compute covariance between two 1D matrices
 * 
 */
public class CoVariance extends Lop 
{

	public CoVariance(Lop input1, DataType dt, ValueType vt, ExecType et) {
		super(Lop.Type.CoVariance, dt, vt);
		init(input1, null, null, et);
	}
	
	public CoVariance(Lop input1, Lop input2, DataType dt, ValueType vt, ExecType et) {
		this(input1, input2, null, dt, vt, et);
	}
	
	public CoVariance(Lop input1, Lop input2, Lop input3, DataType dt, ValueType vt, ExecType et) {
		super(Lop.Type.CoVariance, dt, vt);
		init(input1, input2, input3, et);
	}

	private void init(Lop input1, Lop input2, Lop input3, ExecType et) {
		/*
		 * When et = MR: covariance lop will have a single input lop, which
		 * denote the combined input data -- output of combinebinary, if unweighed;
		 * and output combineteriaty (if weighted).
		 * 
		 * When et = CP: covariance lop must have at least two input lops, which
		 * denote the two input columns on which covariance is computed. It also
		 * takes an optional third arguments, when weighted covariance is computed.
		 */
		addInput(input1);
		input1.addOutput(this);

		if ( input2 == null ) {
			throw new LopsException(this.printErrorLocation() + "Invalid inputs to covariance lop.");
		}
		addInput(input2);
		input2.addOutput(this);
		
		if ( input3 != null ) {
			addInput(input3);
			input3.addOutput(this);
		}
		lps.setProperties(inputs, et);
	}

	@Override
	public String toString() {

		return "Operation = coVariance";
	}
	
	/**
	 * Function two generate CP instruction to compute unweighted covariance.
	 * input1 -&gt; input column 1
	 * input2 -&gt; input column 2
	 */
	@Override
	public String getInstructions(String input1, String input2, String output) {
		return getInstructions(input1, input2, null, output);
	}

	/**
	 * Function two generate CP instruction to compute weighted covariance.
	 * input1 -&gt; input column 1
	 * input2 -&gt; input column 2
	 * input3 -&gt; weights
	 */
	@Override
	public String getInstructions(String input1, String input2, String input3, String output) {
		StringBuilder sb = new StringBuilder();
		sb.append( getExecType() );
		sb.append( Lop.OPERAND_DELIMITOR );
		sb.append( "cov" );
		sb.append( OPERAND_DELIMITOR );

		sb.append( getInputs().get(0).prepInputOperand(input1));
		sb.append( OPERAND_DELIMITOR );

		if( input2 != null ) {
			sb.append( getInputs().get(1).prepInputOperand(input2));
			sb.append( OPERAND_DELIMITOR );
		}
		
		if( input3 != null ) {
			sb.append( getInputs().get(2).prepInputOperand(input3));
			sb.append( OPERAND_DELIMITOR );
		}
		
		sb.append( prepOutputOperand(output));
		
		return sb.toString();
	}
}