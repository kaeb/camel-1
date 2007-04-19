/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.spring.xml;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;

public class BuilderAction {

	private final MethodInfo methodInfo;
	private final HashMap<String, Object> parameterValues;

	public BuilderAction(MethodInfo methodInfo, HashMap<String, Object> parameterValues) {
		this.methodInfo = methodInfo;
		this.parameterValues = parameterValues;
	}

	public Object invoke(BeanFactory beanFactory, Object rootBuilder, Object contextBuilder) {
		SimpleTypeConverter converter = new SimpleTypeConverter();
		Object args[] = new Object[methodInfo.parameters.size()];
		int pos=0;
		for (Map.Entry<String, Class> entry :  methodInfo.parameters.entrySet()) {
			String paramName = entry.getKey();
			Class paramClass = entry.getValue();
			Object value = parameterValues.get(paramName);
			if( value != null ) {
				if( value.getClass() == RuntimeBeanReference.class ) {
					value = beanFactory.getBean(((RuntimeBeanReference)value).getBeanName());
				}
				if( value.getClass() == BuilderStatement.class ) {
					BuilderStatement bs = (BuilderStatement) value;
					value = bs.create(beanFactory, rootBuilder);
				}				
				args[pos] = converter.convertIfNecessary(value, paramClass);				
			}
			
		}
		
		try {
			return methodInfo.method.invoke(contextBuilder, args);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e.getCause());
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
		
	}

	public String getName() {
		return methodInfo.getName();
	}

	public MethodInfo getMethodInfo() {
		return methodInfo;
	}
}
