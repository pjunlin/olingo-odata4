/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.core.serializer.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.AbstractEntityCollection;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.EntityIterator;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.serializer.ChannelSerializerResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class ODataJsonStreamSerializer extends ODataJsonSerializer {

  private final ODataJsonSerializer serializer;

  public ODataJsonStreamSerializer(final ContentType contentType) {
    super(contentType);
    this.serializer = new ODataJsonSerializer(contentType);
  }

  @Override
  public SerializerResult entityCollection(final ServiceMetadata metadata,
      final EdmEntityType entityType, final AbstractEntityCollection entitySet,
      final EntityCollectionSerializerOptions options) throws SerializerException {

    EntityIterator coll;
    if(entitySet instanceof EntityIterator) {
      coll = (EntityIterator) entitySet;
    } else {
      return serializer.entityCollection(metadata, entityType, entitySet, options);
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    SerializerException cachedException = null;
    try {
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();

      final ContextURL contextURL = serializer.checkContextURL(options == null ? null : options.getContextURL());
      serializer.writeContextURL(contextURL, json);

      serializer.writeMetadataETag(metadata, json);

      if (options != null && options.getCount() != null && options.getCount().getValue()) {
        serializer.writeCount(entitySet, json);
      }
      json.writeFieldName(Constants.VALUE);
      json.writeStartArray();
      json.close();
      outputStream.close();
      String temp = new String(outputStream.toByteArray(), Charset.forName("UTF-8"));
      String head = temp.substring(0, temp.length()-2);
      //      if (options == null) {
//        writeEntitySet(metadata, entityType, entitySet, null, null, false, json);
//      } else {
//        writeEntitySet(metadata, entityType, entitySet,
//            options.getExpand(), options.getSelect(), options.getWriteOnlyReferences(), json);
//      }

      outputStream = new ByteArrayOutputStream();
      outputStream.write(']');
      outputStream.write('}');
      outputStream.close();
      String tail = new String(outputStream.toByteArray(), Charset.forName("UTF-8"));

      EntitySerializerOptions.Builder opt = EntitySerializerOptions.with();
      if(options != null) {
        opt.expand(options.getExpand()).select(options
            .getSelect()).writeOnlyReferences(options.getWriteOnlyReferences());
      }
//      return StreamSerializerResult.with(coll, entityType, this, metadata, opt.build())
//          .addHead(head).addTail(tail).build();
      return ChannelSerializerResult.with(coll, entityType, this, metadata, opt.build())
          .addHead(head).addTail(tail).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public void writeEntity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final ContextURL contextURL, final ExpandOption expand,
      final SelectOption select, final boolean onlyReference, final JsonGenerator json)
      throws IOException, SerializerException {
    serializer.writeEntity(metadata, entityType, entity, contextURL, expand, select, onlyReference, json);
  }

//  @Override
//  public SerializerResult entity(final ServiceMetadata metadata, final EdmEntityType entityType,
//      final Entity entity, final EntitySerializerOptions options) throws SerializerException {
//    return serializer.entity(metadata, entityType, entity, options);
//  }

//  protected void writeEntitySet(final ServiceMetadata metadata, final EdmEntityType entityType,
//      final EntityCollection entitySet, final ExpandOption expand, final SelectOption select,
//      final boolean onlyReference, final JsonGenerator json) throws IOException,
//      SerializerException {
//
//        json.writeStartArray();
//        json.writeEndArray();
//
    //    json.writeStartArray();
//    for (final Entity entity : entitySet.getEntities()) {
//      if (onlyReference) {
//        json.writeStartObject();
//        json.writeStringField(Constants.JSON_ID, entity.getId().toASCIIString());
//        json.writeEndObject();
//      } else {
//        serializer.writeEntity(metadata, entityType, entity, null, expand, select, false, json);
//      }
//    }
//    json.writeEndArray();
//  }

}