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

package org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types;

import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.EnumType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;


@Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@EnumType(name = "CompanyCategory",
          underlyingType = EdmPrimitiveTypeKind.Int32,
          isFlags = false)
public enum CompanyCategory {
    IT(0),
    Communication(1),
    Electronics(2),
    Others(4);

    private Integer value;
    
    public Integer getValue(){
      return this.value;
    }

    private CompanyCategory(final Integer value){
      this.value=value;
    }
}
