package com.netflix.spinnaker.front50.model.pipeline

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.front50.api.model.pipeline.Pipeline
import com.netflix.spinnaker.front50.api.model.pipeline.Trigger
import com.netflix.spinnaker.front50.jackson.mixins.PipelineMixins
import spock.lang.Specification

class PipelineSpec extends Specification {
  ObjectMapper objectMapper = new ObjectMapper()

  void setup() {
    objectMapper.addMixIn(Pipeline.class, PipelineMixins.class)
  }

  def 'should ignore correct pipeline properties when serializing Pipeline to JSON'() {
    given:
    Pipeline pipeline = new Pipeline()
    pipeline.setId("1")
    String pipelineJSON = objectMapper.writeValueAsString(pipeline)

    expect:
    pipelineJSON == '{"disabled":null,"triggers":[],"lastModifiedBy":null,"template":null,"roles":null,"serviceAccount":null,"executionEngine":null,"stageCounter":null,"stages":null,"constraints":null,"payloadConstraints":null,"keepWaitingPipelines":null,"limitConcurrent":null,"parameterConfig":null,"spelEvaluator":null,"lastModified":null,"createdAt":null}'
  }

  def 'should ignore correct pipeline properties from deserializing JSON to Pipeline'() {
    given:
    String pipelineJSON = '{"foo": "bar"}'

    Pipeline pipeline = objectMapper.readValue(pipelineJSON, Pipeline.class)

    expect:
    pipeline.getType() == null
  }

  def 'should set any additional pipeline properties when deserializing JSON to Pipeline'() {
    given:
    String pipelineJSON = '{"foo": "bar"}'

    Pipeline pipeline = objectMapper.readValue(pipelineJSON, Pipeline.class)

    expect:
    pipeline.getAny() == [foo: "bar"]
  }

  def 'roundtrip (JSON -> Pipeline -> JSON) retains arbitrary values'() {
    given:
    String pipelineJSON = '{"disabled":null,"triggers":[],"lastModifiedBy":"anonymous","template":null,"roles":null,"serviceAccount":null,"executionEngine":null,"stageCounter":null,"stages":null,"constraints":null,"payloadConstraints":null,"keepWaitingPipelines":null,"limitConcurrent":null,"parameterConfig":null,"spelEvaluator":null,"lastModified":null,"createdAt":null,"foo":"bar"}'

    String pipeline = objectMapper.writeValueAsString(objectMapper.readValue(pipelineJSON, Pipeline.class))

    expect:
    pipeline == pipelineJSON
  }

  def 'should grab triggers after deserializing JSON into Pipeline'() {
    given:
    String pipelineJSON = '{"triggers": [{"type": "cron", "id": "a"}, {"type": "cron", "id": "b"}]}'

    ArrayList<Trigger> triggers = new ArrayList<Trigger>();
    Trigger triggerA = new Trigger();
    triggerA.put("type", "cron");
    triggerA.put("id", "a");
    triggers.add(triggerA);

    Trigger triggerB = new Trigger();
    triggerB.put("type", "cron");
    triggerB.put("id", "b");
    triggers.add(triggerB);

    Pipeline pipeline = objectMapper.readValue(pipelineJSON, Pipeline.class)

    expect:
    pipeline.getTriggers() == triggers
  }
}
