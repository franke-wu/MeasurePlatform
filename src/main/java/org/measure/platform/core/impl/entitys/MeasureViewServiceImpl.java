package org.measure.platform.core.impl.entitys;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.measure.platform.core.api.entitys.AnalysisCardService;
import org.measure.platform.core.api.entitys.DashboardService;
import org.measure.platform.core.api.entitys.MeasureViewService;
import org.measure.platform.core.api.entitys.PhaseService;
import org.measure.platform.core.api.entitys.ProjectService;
import org.measure.platform.core.entity.AnalysisCard;
import org.measure.platform.core.entity.Dashboard;
import org.measure.platform.core.entity.MeasureView;
import org.measure.platform.core.entity.Phase;
import org.measure.platform.core.entity.Project;
import org.measure.platform.core.impl.repository.MeasureViewRepository;
import org.measure.platform.service.measurement.api.IElasticsearchIndexManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing MeasureView.
 */
@Service
@Transactional
public class MeasureViewServiceImpl implements MeasureViewService {
    private final Logger log = LoggerFactory.getLogger(MeasureViewServiceImpl.class);

    @Autowired
    private MessageSource messageSource;

    @Value("${measure.kibana.adress}")
    private String kibanaAdress;

    @Inject
    private MeasureViewRepository measureViewRepository;

    @Inject
    private ProjectService projectService;
    
    @Inject
    private AnalysisCardService analysisCardService;

    @Inject
    private PhaseService phaseService;

    @Inject
    private DashboardService dashboardService;

    @Inject
    private IElasticsearchIndexManager indexManager;

    /**
     * Save a measureView.
     * @param measureView the entity to save
     * @return the persisted entity
     */
    public MeasureView save(MeasureView measureView) {
        log.debug("Request to save MeasureView : {}", measureView);
        
        String mode = measureView.getMode();
        if ("AUTO".equals(mode)) {
            updateViewData(measureView);
        } else if ("KVIS".equals(mode)) {
            updateViewDataFromKibanaVisualisation(measureView);
        } else if ("KDASH".equals(mode)) {
            updateViewDataFromKibanaDashboard(measureView);
        } else if ("CARD".equals(mode)) {
            updateViewDataFromAnalysisCard(measureView);
        }
        
        MeasureView result = measureViewRepository.save(measureView);
        return result;
    }

    private void updateViewDataFromAnalysisCard(MeasureView measureView) {
       AnalysisCard card = measureView.getAnalysiscard();
       String value = messageSource.getMessage("viewtype.view5", new Object[] {card.getCardUrl(),card.getPreferedHeight(), card.getPreferedWidth()}, Locale.ENGLISH);
       measureView.setViewData(value);
      
	}

	private void updateViewData(MeasureView measureView) {
        String type = "line";
        
        if (measureView.getType().equals("Last Value")) {
        
            String refresh = measureView.isAuto() ? "f" : "t";
            String periode = "from:now-1y,mode:quick,to:now";
            String measure = measureView.getMeasureinstance().getInstanceName().replaceAll(" ", "+");
        
            String visualisedProperty = measureView.getVisualisedProperty();
        
            String color = measureView.getColor();
        
            String width = "300";
            String height = "200";
            String font = "50";
        
            if (measureView.getSize().equals("Small")) {
                font = "20";
            } else if (measureView.getSize().equals("Medium")) {
                font = "50";
            } else if (measureView.getSize().equals("Large")) {
                font = "80";
            } else if (measureView.getSize().equals("Very Large")) {
                font = "120";
            }
        
            String value = messageSource.getMessage("viewtype.view2", new Object[] { "metric", refresh, periode,
                    measure, font, height, width, kibanaAdress, visualisedProperty, color,indexManager.getBaseMeasureIndex() }, Locale.ENGLISH);
            measureView.setViewData(value);
        } else {
            if (measureView.getType().equals("Line chart")) {
                type = "line";
            } else if (measureView.getType().equals("Area chart")) {
                type = "area";
            } else if (measureView.getType().equals("Bar chart")) {
                type = "histogram";
            }
        
            String refresh = measureView.isAuto() ? "f" : "t";
            
            String periode = measureView.getTimePeriode();
            String interval = measureView.getTimeAgregation();
        
            String measure = measureView.getMeasureinstance().getInstanceName().replaceAll(" ", "+");
        
            String color = measureView.getColor();
        
            String width = "800";
            String height = "400";
            if (measureView.getSize().equals("Small")) {
                width = "300";
                height = "200";
            } else if (measureView.getSize().equals("Medium")) {
                width = "400";
                height = "300";
            } else if (measureView.getSize().equals("Large")) {
                width = "600";
                height = "400";
            } else if (measureView.getSize().equals("Very Large")) {
                width = "800";
                height = "600";
            }
        
            String visualisedProperty = measureView.getVisualisedProperty();
            String dateIndex = measureView.getDateIndex();
        
            String value = messageSource.getMessage("viewtype.view1", new Object[] { type, refresh, periode, measure,color, interval, height, width, kibanaAdress, visualisedProperty, dateIndex,indexManager.getBaseMeasureIndex() }, Locale.ENGLISH);
            measureView.setViewData(value);
        }
    }

    private void updateViewDataFromKibanaVisualisation(MeasureView measureView) {
        String width = "800";
        String height = "400";
        if (measureView.getSize().equals("Small")) {
            width = "300";
            height = "200";
        } else if (measureView.getSize().equals("Medium")) {
            width = "400";
            height = "300";
        } else if (measureView.getSize().equals("Large")) {
            width = "600";
            height = "400";
        } else if (measureView.getSize().equals("Very Large")) {
            width = "800";
            height = "600";
        }
            
        String periode = measureView.getTimePeriode();
        String refresh = measureView.isAuto() ? "f" : "t";
            
        String value = messageSource.getMessage("viewtype.view3",
                new Object[] { height, width, kibanaAdress, measureView.getKibanaName(),refresh ,periode}, Locale.ENGLISH);
        measureView.setViewData(value);
    }

    private void updateViewDataFromKibanaDashboard(MeasureView measureView) {
        String height = measureView.getSize();
                
        String periode = measureView.getTimePeriode();
        String refresh = measureView.isAuto() ? "f" : "t";
        
        String value = messageSource.getMessage("viewtype.view4",new Object[] { height, kibanaAdress, measureView.getKibanaName(),refresh,periode }, Locale.ENGLISH);
        measureView.setViewData(value);
    }

    /**
     * Get all the measureViews.
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<MeasureView> findAll() {
        log.debug("Request to get all MeasureViews");
        List<MeasureView> result = measureViewRepository.findAll();
        return result;
    }

    /**
     * Get one measureView by id.
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public MeasureView findOne(Long id) {
        log.debug("Request to get MeasureView : {}", id);
        MeasureView measureView = measureViewRepository.findOne(id);
        return measureView;
    }

    /**
     * Delete the measureView by id.
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete MeasureView : {}", id);
        measureViewRepository.delete(id);
    }

    public List<MeasureView> findByProject(Long id) {
        Project project = projectService.findOne(id);
        return measureViewRepository.findByProject(project);
    }

    public List<MeasureView> findByProjectOverview(Long id) {
        Project project = projectService.findOne(id);
        return measureViewRepository.findByProjectOverview(project);
    }

    public List<MeasureView> findByPhase(Long id) {
        Phase phase = phaseService.findOne(id);
        return measureViewRepository.findByPhase(phase);
    }

    public List<MeasureView> findByPhaseOverview(Long id) {
        Phase phase = phaseService.findOne(id);
        return measureViewRepository.findByPhaseOverview(phase);
    }

    public List<MeasureView> findByDashboard(Long id) {
        Dashboard dashboard = dashboardService.findOne(id);
        return measureViewRepository.findByDashboard(dashboard);
    }
    
	@Override
	public List<MeasureView> findByAnalysisCard(Long id) {
		AnalysisCard analysisCard = analysisCardService.findOne(id);
		return measureViewRepository.findByAnalysisCard(analysisCard);
	}



}
