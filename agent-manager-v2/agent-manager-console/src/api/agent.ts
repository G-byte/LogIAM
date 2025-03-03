import fetch from "../lib/fetch";
import { apiMap, csrfTokenMethod } from "./api";
import { IAgentHostParams, IOperationTasksParams, IEditOpHostsParams, IOpAgent, IAddOpHostsParams } from '../interface/agent';

export interface IAgentParam {
  pageNo: number;
  pageSize: number;
}

export const getAgent = (params: IAgentHostParams) => {
  return fetch(apiMap.getAgentHostList, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

export const getAgentVersion = () => {
  return fetch(apiMap.getAgentVersionList);
}

export const getServices = () => {
  return fetch(apiMap.getServicesList);
}

export const createOperationTasks = (params: IOperationTasksParams) => {
  return fetch(apiMap.createOpTasks, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

export const getHostMachineZone = () => {
  return fetch(apiMap.getHostMachineZones);
}

export const editOpHosts = (params: IEditOpHostsParams) => {
  return fetch(apiMap.editOpHosts, {
    method: csrfTokenMethod[1],
    body: JSON.stringify(params),
  });
}

export const getAgentDetails = (agentId: number) => {
  return fetch(apiMap.getAgentDetail + `/${agentId}`);
}

export const editOpAgent = (params: IOpAgent) => {
  return fetch(apiMap.editOpAgent, {
    method: csrfTokenMethod[1],
    body: JSON.stringify(params),
  });
}

export const getReceivers = () => {
  return fetch(apiMap.getReceiversList);
}

export const getReceiversTopic = (receiverId: number) => {
  return fetch(apiMap.getReceiversTopics + `/${receiverId}/topics`);
}

export const addOpHosts = (params: IAddOpHostsParams) => {
  return fetch(apiMap.addOpHost, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
    needDuration: true,
  });
}

export const testHost = (hostname: string) => {
  return fetch(apiMap.TestEdHost + `/${hostname}`);
}

export const getHosts = () => {
  return fetch(apiMap.getHostList);
}

export const deleteHost = (hostId: number, ignoreUncompleteCollect: number) => {
  return fetch(apiMap.deleteHosts + `/${hostId}?ignoreUncompleteCollect=${ignoreUncompleteCollect}`, {
    method: csrfTokenMethod[2],
    needCode: true,
  });
}

export const getHostDetails = (hostId: number) => {
  return fetch(apiMap.getHostDetail + `/${hostId}`);
}

export const getAgentMetrics = (agentId: number, startTime: number, endTime: number) => {
  return fetch(apiMap.getAgentMetrics + `/${agentId}/metrics/${startTime}/${endTime}`);
}

export const getCollectMetrics = (logCollectTaskId: number, startTime: number, endTime: number) => {
  return fetch(apiMap.getCollectMetrics + `/${logCollectTaskId}/metrics/${startTime}/${endTime}`);
}

export const getTaskExists = (agentIdListJsonString: string) => {
  return fetch(apiMap.getCollectTaskExists + `?agentIdListJsonString=${agentIdListJsonString}`);
}

export const getHostListbyServiceId = (appId: number) => {
  return fetch(`${apiMap.getServiceDetail}/${appId}`);
}