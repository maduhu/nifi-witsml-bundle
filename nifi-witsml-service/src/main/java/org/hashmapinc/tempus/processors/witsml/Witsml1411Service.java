package org.hashmapinc.tempus.processors.witsml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmapinc.tempus.WitsmlObjects.Util.log.LogDataHelper;
import com.hashmapinc.tempus.WitsmlObjects.v1411.*;
import com.hashmapinc.tempus.witsml.api.*;
import javafx.scene.control.IndexRange;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnDisabled;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.reporting.InitializationException;

import com.hashmapinc.tempus.witsml.client.Client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Chris on 6/2/17.
 */

@Tags({"WITSML", "Well", "Wellbore", "Log", "Mudlog", "Trajectory", "Version1.4.1.1"})
@CapabilityDescription("Provides session management for Witsml processors")
public class Witsml1411Service extends AbstractControllerService implements IWitsmlServiceApi{

    // Global session variables used by all processors using an instance
    private static Client myClient = null;
    private static ObjectMapper mapper = new ObjectMapper();


    //Properties
    public static final PropertyDescriptor ENDPOINT_URL = new PropertyDescriptor
            .Builder().name("Endpoint URL")
            .description("Specify the witsml.tcp address of the witsml server")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor USERNAME = new PropertyDescriptor
            .Builder().name("Username")
            .description("Specify the username for Witsml Server")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor PASSWORD = new PropertyDescriptor
            .Builder().name("Password")
            .description("Specify the password for Witsml Server")
            .sensitive(true)
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    private static final List<PropertyDescriptor> properties;

    static {
        final List<PropertyDescriptor> props = new ArrayList<>();
        props.add(ENDPOINT_URL);
        props.add(USERNAME);
        props.add(PASSWORD);
        properties = Collections.unmodifiableList(props);
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return properties;
    }

    @OnEnabled
    public void onEnabled(final ConfigurationContext context) throws InitializationException {
        final ComponentLog logger = getLogger();
        logger.info("Creating Witsml Client");

        myClient = new Client(context.getProperty(ENDPOINT_URL).getValue());
        myClient.setUserName(context.getProperty(USERNAME).getValue());
        myClient.setPassword(context.getProperty(PASSWORD).getValue());
        myClient.setVersion(WitsmlVersion.VERSION_1411);
        myClient.connect();
        setMapper();
    }

    @Override
    public String getObject(String wellId, String wellboreId, String object) {
        try {
            switch (object) {
                case "ATTACHMENT" :
                    ObjAttachments attachments = myClient.getAttachmentsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(attachments);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting Attachments Object to Json" + ex);
                    }
                    break;
                case "BHARUN":
                    ObjBhaRuns bhaRuns = myClient.getBhaRunsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(bhaRuns);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting BhaRuns Object to Json" + ex);
                    }
                    break;
                case "CEMENTJOB":
                    ObjCementJobs cementJobs = myClient.getCementJobsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(cementJobs);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting CementJobs to Json : " + ex);
                    }
                    break;
                case "CHANGELOG" :
                    ObjChangeLogs changeLogs = myClient.getChangeLogsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(changeLogs);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting ChangeLogs Object to Json" + ex);
                    }
                    break;
                case "CONVCORE":
                    ObjConvCores convCores = myClient.getConvCoresAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(convCores);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting ConvCores to Json : " + ex);
                    }
                    break;
                case "DRILLREPORT" :
                    ObjDrillReports drillReports = myClient.getDrillReportsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(drillReports);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting DrillReports Object to Json" + ex);
                    }
                    break;
                case "FLUIDREPORT":
                    ObjFluidsReports fluidsReports = myClient.getFluidsReportsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(fluidsReports);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting FluidsReports to Json : " + ex);
                    }
                    break;
                case "FORMATIONMARKER":
                    ObjFormationMarkers formationMarkers = myClient.getFormationMarkersAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(formationMarkers);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting FormationMarkers to Json : " + ex);
                    }
                    break;
                case "LOG":
                    ObjLogs logs = myClient.getLogMetadataAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(logs);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting Logs to Json : " + ex);
                    }
                    break;
                case "MESSAGE":
                    ObjMessages messages = myClient.getMessagesAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(messages);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting Messages to Json : " + ex);
                    }
                    break;
                case "MUDLOG":
                    ObjMudLogs mudLogs = myClient.getMudLogsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(mudLogs);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting MudLogs to Json : " + ex);
                    }
                    break;
                case "OBJECTGROUP" :
                    ObjObjectGroups objObjectGroups = myClient.getObjectGroupsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(objObjectGroups);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting ObjectGroups Object to Json" + ex);
                    }
                    break;
                case "OPSREPORT":
                    ObjOpsReports opsReports = myClient.getOpsReportsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(opsReports);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting OpsReports to Json : " + ex);
                    }
                    break;
                case "RIG":
                    ObjRigs rigs = myClient.getRigsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(rigs);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting Rigs to Json : " + ex);
                    }
                    break;
                case "RISK":
                    ObjRisks risks = myClient.getRisksAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(risks);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting Risks to Json : " + ex);
                    }
                    break;
                case "SIDEWALLCORE":
                    ObjSidewallCores sidewallCores = myClient.getSideWallCoresAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(sidewallCores);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting SideWallCores to Json : " + ex);
                    }
                    break;
                case "STIMJOB" :
                    ObjStimJobs stimJobs = myClient.getStimJobsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(stimJobs);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting StimJobs Object to Json" + ex);
                    }
                    break;
                case "SURVEYPROGRAM":
                    ObjSurveyPrograms surveyPrograms = myClient.getSurveyProgramsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(surveyPrograms);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting SurveyPrograms to Json : " + ex);
                    }
                    break;
                case "TARGET":
                    ObjTargets targets = myClient.getTargetsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(targets);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting Targets to Json : " + ex);
                    }
                    break;
                case "TRAJECTORY":
                    ObjTrajectorys trajectorys = myClient.getTrajectorysAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(trajectorys);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting Trajectorys to Json : " + ex);
                    }
                    break;
                case "TUBULAR":
                    ObjTubulars tubulars = myClient.getTubularsAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(tubulars);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting Tubulars to Json : " + ex);
                    }
                    break;
                case "WBGEOMETRY":
                    ObjWbGeometrys wbGeometrys = myClient.getWbGeometrysAsObj(wellId, wellboreId);
                    try {
                        return mapper.writeValueAsString(wbGeometrys);
                    } catch (JsonProcessingException ex) {
                        getLogger().error("Error in converting WbGeometrys to Json : " + ex);
                    }
                    break;
                default:
                    getLogger().error("The Object : " + object + " is not supported/present");
                    break;
            }

        } catch (Exception ex) {
            getLogger().error("Error in getting data from WITSML server");
        }
        return null;
    }

    @Override
    public ObjLogs getLogData(String wellId, String wellboreId, String logId, LogRequestTracker logTracker) {
        logTracker.setVersion(WitsmlVersion.VERSION_1411);
        logTracker.setLogId(logId);
        logTracker.initalize(myClient, wellId, wellboreId);

        return logTracker.ExecuteRequest();
    }

    @Override
    public ObjMudLogs getMudLogData(String wellId, String wellboreId, String mudLogId, MudlogRequestTracker mudLogTracker) {
        mudLogTracker.setVersion(WitsmlVersion.VERSION_1411);
        mudLogTracker.setMudlogId(mudLogId);
        mudLogTracker.initalize(myClient, wellId, wellboreId);

        return mudLogTracker.ExecuteRequest();
    }

    @Override
    public ObjTrajectorys getTrajectoryData(String wellId, String wellboreId, String trajectoryid, TrajectoryRequestTracker trajectoryTracker) {
        trajectoryTracker.setVersion(WitsmlVersion.VERSION_1411);
        trajectoryTracker.setTrajectoryId(trajectoryid);
        trajectoryTracker.initalize(myClient, wellId, wellboreId);

        return trajectoryTracker.ExecuteRequest();
    }

    @Override
    public List<WitsmlObjectId> getAvailableObjects(String uri, List<String> objectTypes, String wellFilter) {
        return getAvailableObjects(uri, objectTypes, "");
    }

    public List<WitsmlObjectId> getAvailableObjects(String uri, List<String> objectTypes){
        QueryTarget target = QueryTarget.parseURI(uri, objectTypes);
        List<WitsmlObjectId> ids = new ArrayList<>();
        switch (target.getQueryLevel()){
            case Server: {
                ObjWells wells = getWellData();
                if (wells == null)
                    return null;
                for (ObjWell w:wells.getWell()) {
                    if (w == null)
                        continue;
                    WitsmlObjectId objId = new WitsmlObjectId(w.getName(), w.getUid(), "well", "");
                    ids.add(objId);
                }
                break;
            }
            case Well: {
                ObjWellbores wellbores = getWellboreData(target.getWell());
                if (wellbores == null)
                    return null;
                for (ObjWellbore wb:wellbores.getWellbore()){
                    if (wb == null)
                        continue;
                    WitsmlObjectId objId = new WitsmlObjectId(wb.getName(), wb.getUid(), "wellbore", "/" + wb.getNameWell() + "(" + wb.getUidWell() + ")");
                    ids.add(objId);
                }
                break;
            }
            case Wellbore:
                ids = queryForTypes(target);
                break;
        }
        return ids;
    }

    private ObjWells getWellData(){
        try {
            return myClient.getWellsAsObj();
        } catch (Exception e) {
            getLogger().error("Error in getWells: " + e.getMessage());
            return null;
        }
    }

    private ObjWellbores getWellboreData(WitsmlObjectId well){
        try {
            return myClient.getWellboresForWellAsObj(well.getId());
        } catch (Exception e) {
            getLogger().error("Error in getWellbores: " + e.getMessage());
            return null;
        }
    }

    private List<WitsmlObjectId> queryForTypes(QueryTarget targetObj){
        List<WitsmlObjectId> ids = new ArrayList<>();
        List<String> types = targetObj.getObjectsToQuery();
        String wellId = targetObj.getWell().getId();
        String wellboreId = targetObj.getWellbore().getId();
        String parentURI = "/" + targetObj.getWell().getName() + "(" + targetObj.getWell().getId() + ")/" + targetObj.getWellbore().getName() + "(" + targetObj.getWellbore().getId() + ")";

        for (String type : types) {
            try {
                switch (type.toUpperCase()) {
                    case "ATTACHMENT" :
                        ObjAttachments attachments = myClient.getAttachmentsAsObj(wellId, wellboreId);
                        if (attachments == null) {
                            continue;
                        }
                        for (ObjAttachment attachment : attachments.getAttachment()) {
                            if (attachment == null)
                                continue;
                            ids.add(new WitsmlObjectId(attachment.getName(), attachment.getUid(), "attachment", parentURI));
                        }
                    case "BHARUN":
                        ObjBhaRuns bhaRuns = myClient.getBhaRunsAsObj(wellId, wellboreId);
                        if (bhaRuns == null) {
                            continue;
                        }
                        for(ObjBhaRun bhaRun: bhaRuns.getBhaRun()) {
                            if (bhaRun == null)
                                continue;
                            ids.add(new WitsmlObjectId(bhaRun.getName(), bhaRun.getUid(), "bhaRun", parentURI));
                        }
                        break;
                    case "CEMENTJOB":
                        ObjCementJobs cementJobs = myClient.getCementJobsAsObj(wellId, wellboreId);
                        if (cementJobs == null){
                            continue;
                        }
                        for (ObjCementJob cementJob : cementJobs.getCementJob()) {
                            if (cementJob == null)
                                continue;
                            ids.add(new WitsmlObjectId(cementJob.getName(), cementJob.getUid(), "cementJob", parentURI));
                        }
                        break;
                    case "CHANGELOG" :
                        ObjChangeLogs changeLogs = myClient.getChangeLogsAsObj(wellId, wellboreId);
                        if (changeLogs == null) {
                            continue;
                        }
                        for (ObjChangeLog changeLog: changeLogs.getChangeLog()) {
                            if (changeLog == null)
                                continue;
                            ids.add(new WitsmlObjectId(changeLog.getNameObject(), changeLog.getUid(), "changeLog", parentURI));
                        }
                    case "CONVCORE":
                        ObjConvCores convCores = myClient.getConvCoresAsObj(wellId, wellboreId);
                        if (convCores == null){
                            continue;
                        }
                        for (ObjConvCore convCore : convCores.getConvCore()) {
                            if (convCore == null) {
                                continue;
                            }
                            ids.add(new WitsmlObjectId(convCore.getName(), convCore.getUid(), "convCore", parentURI));
                        }
                        break;
                    case "DRILLREPORT":
                        ObjDrillReports drillReports = myClient.getDrillReportsAsObj(wellId,wellboreId);
                        if (drillReports == null) {
                            continue;
                        }
                        for (ObjDrillReport drillReport : drillReports.getDrillReport()) {
                            if (drillReport == null)
                                continue;
                            ids.add(new WitsmlObjectId(drillReport.getName(), drillReport.getUid(), "drillReport", parentURI));
                        }
                        break;
                    case "FLUIDREPORT":
                        ObjFluidsReports fluidsReports = myClient.getFluidsReportsAsObj(wellId, wellboreId);
                        if (fluidsReports == null){
                            continue;
                        }
                        for (ObjFluidsReport fluidsReport : fluidsReports.getFluidsReport()) {
                            if (fluidsReport == null)
                                continue;
                            ids.add(new WitsmlObjectId(fluidsReport.getName(), fluidsReport.getUid(), "fluidsReport", parentURI));
                        }
                        break;
                    case "FORMATIONMARKER":
                        ObjFormationMarkers formationMarkers = myClient.getFormationMarkersAsObj(wellId, wellboreId);
                        if (formationMarkers == null) {
                            continue;
                        }
                        for (ObjFormationMarker formationMarker: formationMarkers.getFormationMarker()) {
                            if (formationMarker == null)
                                continue;
                            ids.add(new WitsmlObjectId(formationMarker.getName(), formationMarker.getUid(), "formationMarker", parentURI));
                        }
                        break;
                    case "LOG":
                        ObjLogs logs = myClient.getLogMetadataAsObj(wellId, wellboreId);
                        if (logs == null) {
                            continue;
                        }
                        for (ObjLog log : logs.getLog()) {
                            if (log == null)
                                continue;
                            ids.add(new WitsmlObjectId(log.getName(), log.getUid(), "log", parentURI));
                        }
                        break;
                    case "MESSAGE":
                        ObjMessages messages = myClient.getMessagesAsObj(wellId, wellboreId);
                        if (messages == null) {
                            continue;
                        }
                        for (ObjMessage message : messages.getMessage()) {
                            if (message == null)
                                continue;
                            ids.add(new WitsmlObjectId(message.getName(), message.getUid(), "message", parentURI));
                        }
                        break;
                    case "MUDLOG":
                        ObjMudLogs mudLogs = myClient.getMudLogsAsObj(wellId, wellboreId);
                        if (mudLogs == null) {
                            continue;
                        }
                        for (ObjMudLog mudLog: mudLogs.getMudLog()) {
                            if (mudLog == null)
                                continue;
                            ids.add(new WitsmlObjectId(mudLog.getName(), mudLog.getUid(), "mudLog", parentURI));
                        }
                        break;
                    case "OBJECTGROUP":
                        ObjObjectGroups objectGroups = myClient.getObjectGroupsAsObj(wellId, wellboreId);
                        if (objectGroups == null) {
                            continue;
                        }
                        for (ObjObjectGroup objectGroup : objectGroups.getObjectGroup()) {
                            if (objectGroup == null)
                                continue;
                            ids.add(new WitsmlObjectId(objectGroup.getName(), objectGroup.getUid(), "objectGroup", parentURI));
                        }
                    case "OPSREPORT":
                        ObjOpsReports opsReports = myClient.getOpsReportsAsObj(wellId, wellboreId);
                        if (opsReports == null) {
                            continue;
                        }
                        for (ObjOpsReport opsReport: opsReports.getOpsReport()) {
                            if (opsReport == null)
                                continue;
                            ids.add(new WitsmlObjectId(opsReport.getName(), opsReport.getUid(), "opsReport", parentURI));
                        }
                        break;
                    case "RIG":
                        ObjRigs rigs = myClient.getRigsAsObj(wellId, wellboreId);
                        if (rigs == null) {
                            continue;
                        }
                        for (ObjRig rig : rigs.getRig()) {
                            if (rig == null)
                                continue;
                            ids.add(new WitsmlObjectId(rig.getName(), rig.getUid(), "rig", parentURI));
                        }
                        break;
                    case "RISK":
                        ObjRisks risks = myClient.getRisksAsObj(wellId, wellboreId);
                        if (risks == null) {
                            continue;
                        }
                        for (ObjRisk risk : risks.getRisk()) {
                            if (risk == null)
                                continue;
                            ids.add(new WitsmlObjectId(risk.getName(), risk.getUid(), "risk", parentURI));
                        }
                        break;
                    case "SIDEWALLCORE":
                        ObjSidewallCores sidewallCores = myClient.getSideWallCoresAsObj(wellId, wellboreId);
                        if (sidewallCores == null) {
                            continue;
                        }
                        for (ObjSidewallCore sidewallCore : sidewallCores.getSidewallCore()) {
                            if (sidewallCore == null)
                                continue;
                            ids.add(new WitsmlObjectId(sidewallCore.getName(), sidewallCore.getUid(), "sidewallCore", parentURI));
                        }
                        break;
                    case "STIMJOB":
                        ObjStimJobs stimJobs = myClient.getStimJobsAsObj(wellId, wellboreId);
                        if (stimJobs == null) {
                            continue;
                        }
                        for (ObjStimJob stimJob : stimJobs.getStimJob()) {
                            if (stimJob == null)
                                continue;
                            ids.add(new WitsmlObjectId(stimJob.getName(), stimJob.getUid(),"stimJob", parentURI));
                        }
                    case "SURVEYPROGRAM":
                        ObjSurveyPrograms surveyPrograms = myClient.getSurveyProgramsAsObj(wellId, wellboreId);
                        if (surveyPrograms == null) {
                            continue;
                        }
                        for (ObjSurveyProgram surveyProgram: surveyPrograms.getSurveyProgram()) {
                            if (surveyProgram == null)
                                continue;
                            ids.add(new WitsmlObjectId(surveyProgram.getName(), surveyProgram.getUid(), "surveyProgram", parentURI));
                        }
                        break;
                    case "TARGET":
                        ObjTargets targets = myClient.getTargetsAsObj(wellId, wellboreId);
                        if (targets == null) {
                            continue;
                        }
                        for (ObjTarget target : targets.getTarget()) {
                            if (target == null)
                                continue;
                            ids.add(new WitsmlObjectId(target.getName(), target.getUid(), "target", parentURI));
                        }
                        break;
                    case "TRAJECTORY":
                        ObjTrajectorys trajectorys = myClient.getTrajectorysAsObj(wellId, wellboreId);
                        if (trajectorys == null) {
                            continue;
                        }
                        for (ObjTrajectory trajectory: trajectorys.getTrajectory()) {
                            if (trajectory == null)
                                continue;
                            ids.add(new WitsmlObjectId(trajectory.getName(), trajectory.getUid(), "trajectory", parentURI));
                        }
                        break;
                    case "TUBULAR":
                        ObjTubulars tubulars = myClient.getTubularsAsObj(wellId, wellboreId);
                        if (tubulars == null) {
                            continue;
                        }
                        for (ObjTubular tubular: tubulars.getTubular()) {
                            if (tubular == null)
                                continue;
                            ids.add(new WitsmlObjectId(tubular.getName(), tubular.getUid(), "tubular", parentURI));
                        }
                        break;
                    case "WBGEOMETRY":
                        ObjWbGeometrys wbGeometrys = myClient.getWbGeometrysAsObj(wellId, wellboreId);
                        if (wbGeometrys == null) {
                            continue;
                        }
                        for (ObjWbGeometry wbGeometry: wbGeometrys.getWbGeometry()) {
                            if (wbGeometry == null)
                                continue;
                            ids.add(new WitsmlObjectId(wbGeometry.getName(), wbGeometry.getUid(), "wbGeometry", parentURI));
                        }
                        break;
                    default:
                        getLogger().error("The Object : " + type + " is not supported/present");
                        break;
                }
            } catch (Exception ex) {
                getLogger().error("Error in getting data from WITSML server");
            }
        }
        return ids;
    }

    @Override
    public Object getObjectData(String wellId, String wellboreId, String objType, String objectId, ObjectRequestTracker objectTracker) {
        objectTracker.initalize(myClient, wellId, wellboreId);
        objectTracker.setVersion(WitsmlVersion.VERSION_1411);
        objectTracker.setObjectId(objectId);
        objectTracker.setObjectType(objType);
        return objectTracker.ExecuteRequest();
    }

    @Override
    public String getWell(String wellId, String status) {
        return null;
    }

    @Override
    public String getWellbore(String wellId, String wellboreId) {
        return null;
    }

    private void setMapper() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }
}
