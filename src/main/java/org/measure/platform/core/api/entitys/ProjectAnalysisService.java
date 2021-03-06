package org.measure.platform.core.api.entitys;

import java.util.List;

import org.measure.platform.core.entity.Project;
import org.measure.platform.core.entity.ProjectAnalysis;

/**
 * Service Interface for managing ProjectAnalysis.
 */
public interface ProjectAnalysisService {
    /**
     * Save a Project Analysis.
     * @param project the entity to save
     * @return the persisted entity
     */
	ProjectAnalysis save(ProjectAnalysis project);

    /**
     * Get all the Project Analysis.
     * @return the list of entities
     */
    List<ProjectAnalysis> findAll();

    /**
     * Get all the Project Analysis of current owner.
     * @return the list of entities
     */
    List<ProjectAnalysis> findAllByProject(Project project);

    /**
     * Get the "id" Project Analysis.
     * @param id the id of the entity
     * @return the entity
     */
    ProjectAnalysis findOne(Long id);

    /**
     * Delete the "id" project.
     * @param id the id of the entity
     */
    void delete(Long id);

}
