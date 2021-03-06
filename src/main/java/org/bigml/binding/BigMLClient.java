package org.bigml.binding;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.bigml.binding.resources.Dataset;
import org.bigml.binding.resources.Evaluation;
import org.bigml.binding.resources.Model;
import org.bigml.binding.resources.Prediction;
import org.bigml.binding.resources.Source;
import org.json.simple.JSONObject;

/**
 * Entry point to create, retrieve, list, update, and delete sources, datasets, models,
 * predictions and evaluations.
 *
 * Full API documentation on the API can be found from BigML at: https://bigml.com/developers
 *
 *
 */
public class BigMLClient {

  /**
   * Logging
   */
  static Logger logger = Logger.getLogger(BigMLClient.class.getName());
  
  private static BigMLClient instance = null;
  
  private String bigmlUrl;
  private String bigmlUser;
  private String bigmlApiKey;
  private Source source;
  private Dataset dataset;
  private Model model;
  private Prediction prediction;
  private Evaluation evaluation;
  private Properties props;
  private Boolean devMode = false;

  protected BigMLClient() {
  }

  public static BigMLClient getInstance() throws AuthenticationException {
    if (instance == null) {
      instance = new BigMLClient();
      instance.init(false);
    }
    return instance;
  }
  
  public static BigMLClient getInstance(final boolean devMode) throws AuthenticationException {
    if (instance == null) {
      instance = new BigMLClient();
      instance.init(devMode);
    }
    return instance;
  }

  
  public static BigMLClient getInstance(final String apiUser, final String apiKey, final boolean devMode) throws AuthenticationException {
    if (instance == null) {
      instance = new BigMLClient();
      instance.init(apiUser, apiKey, devMode);
    }
    return instance;
  }

  
  /**
   * Initialization object.
   */
  private void init(final boolean devMode) throws AuthenticationException {
	this.devMode = devMode;
    initConfiguration();

    this.bigmlUser = System.getProperty("BIGML_USERNAME");
    this.bigmlApiKey = System.getProperty("BIGML_API_KEY");
    if (this.bigmlUser == null || this.bigmlUser.equals("") || this.bigmlApiKey == null || this.bigmlApiKey.equals("")) {
      this.bigmlUser = props.getProperty("BIGML_USERNAME");
      this.bigmlApiKey = props.getProperty("BIGML_API_KEY");
      if (this.bigmlUser == null || this.bigmlUser.equals("") || this.bigmlApiKey == null || this.bigmlApiKey.equals("")) {
        AuthenticationException ex = new AuthenticationException("Missing authentication information.");
        logger.info(instance, ex);
        throw ex;
      }
    }

    initResources();
  }

  /**
   * Initialization object.
   */
  private void init(final String apiUser, final String apiKey, final boolean devMode) throws AuthenticationException {
    this.devMode = devMode;
	initConfiguration();

    this.bigmlUser = apiUser != null ? apiUser : System.getProperty("BIGML_USERNAME");
    this.bigmlApiKey = apiKey != null ? apiKey : System.getProperty("BIGML_API_KEY");
    if (this.bigmlUser == null || this.bigmlUser.equals("") || this.bigmlApiKey == null || this.bigmlApiKey.equals("")) {
      this.bigmlUser = props.getProperty("BIGML_USERNAME");
      this.bigmlApiKey = props.getProperty("BIGML_API_KEY");
      if (this.bigmlUser == null || this.bigmlUser.equals("") || this.bigmlApiKey == null || this.bigmlApiKey.equals("")) {
        AuthenticationException ex = new AuthenticationException("Missing authentication information.");
        logger.info(instance, ex);
        throw ex;
      }
    }

    initResources();
  }

  private void initConfiguration() {
    try {
      props = new Properties();
      FileInputStream fis = new FileInputStream(new File("src/main/resources/binding.properties"));
      props.load(fis);
      fis.close();

      bigmlUrl = this.devMode ? props.getProperty("BIGML_DEV_URL") : props.getProperty("BIGML_URL");
    } catch (Throwable e) {
      logger.error("Error loading configuration", e);
    }
  }

  private void initResources() {
    source = new Source(this.bigmlUser, this.bigmlApiKey, this.devMode);
    dataset = new Dataset(this.bigmlUser, this.bigmlApiKey, this.devMode);
    model = new Model(this.bigmlUser, this.bigmlApiKey, this.devMode);
    prediction = new Prediction(this.bigmlUser, this.bigmlApiKey, this.devMode);
    evaluation = new Evaluation(this.bigmlUser, this.bigmlApiKey, this.devMode);
  }

  public String getBigMLUrl() {
    return bigmlUrl;
  }
  

  // ################################################################
  // #
  // # Sources
  // # https://bigml.com/developers/sources
  // #
  // ################################################################
  
  /**
   * Create a new source.
   *
   * POST /andromeda/source?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY; HTTP/1.1 Host: bigml.io
   * Content-Type: multipart/form-data;
   *
   * @param fileName	file containing your data in csv format. It can be compressed, gzipped, or
   * zipped. Required multipart/form-data; charset=utf-8
   * @param name	the name you want to give to the new source. Optional
   * @param sourceParser	set of parameters to parse the source. Optional
   *
   */
  public JSONObject createSource(final String fileName, String name, String sourceParser) {
    return source.createLocalSource(fileName, name, sourceParser);
  }
  
  
  /**
   * Creating a source using a URL.
   * 
   * POST /andromeda/source?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY; HTTP/1.1
   * Host: bigml.io
   * Content-Type: application/json;
   *
   * @param url		url for remote source
   * @param args	set of parameters to create the source. Optional
   *
   */
  public JSONObject createRemoteSource(final String url, final String args) {
    return source.createRemoteSource(url, args);
  }

  
  /**
   * Retrieve a source.
   *
   * GET /andromeda/source/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io
   *
   * @param sourceId a unique identifier in the form source/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject getSource(final String sourceId) {
    return source.get(sourceId);
  }

  
  /**
   * Retrieve a source.
   *
   * GET /andromeda/source/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io
   *
   * @param source a unique identifier in the form source/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject getSource(final JSONObject sourceJSON) {
    return source.get(sourceJSON);
  }

  
  /**
   * Check whether a source' status is FINISHED.
   *
   * @param sourceId a unique identifier in the form source/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public boolean sourceIsReady(final String sourceId) {
    return source.isReady(sourceId);
  }

  
  /**
   * Check whether a source' status is FINISHED.
   *
   * @param source a unique identifier in the form source/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public boolean sourceIsReady(final JSONObject sourceJSON) {
    return source.isReady(sourceJSON);
  }

  
  /**
   * List all your sources.
   *
   * GET /andromeda/source?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY; Host: bigml.io
   *
   * @param queryString	query filtering the listing.
   *
   */
  public JSONObject listSources(final String queryString) {
    return source.list(queryString);
  }

  
  /**
   * Update a source.
   *
   * POST
   * /andromeda/source/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io Content-Type: application/json
   *
   * @param sourceId a unique identifier in the form source/id where id is a string of 24
   * alpha-numeric chars.
   * @param body	set of parameters to update the source. Optional
   *
   */
  public JSONObject updateSource(final String sourceId, final String body) {
    return source.update(sourceId, body);
  }

  
  /**
   * Update a source.
   *
   * POST
   * /andromeda/source/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io Content-Type: application/json
   *
   * @param source a unique identifier in the form source/id where id is a string of 24
   * alpha-numeric chars.
   * @param body	set of parameters to update the source. Optional
   *
   */
  public JSONObject updateSource(final JSONObject sourceJSON, final JSONObject json) {
    return source.update(sourceJSON, json);
  }

  
  /**
   * Delete a source.
   *
   * DELETE
   * /andromeda/source/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1
   *
   * @param sourceId a unique identifier in the form source/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject deleteSource(final String sourceId) {
    return source.delete(sourceId);
  }

  
  /**
   * Delete a source.
   *
   * DELETE
   * /andromeda/source/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1
   *
   * @param source a unique identifier in the form source/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject delete(final JSONObject sourceJSON) {
    return source.delete(sourceJSON);
  }

  
  // ################################################################
  // #
  // # Datasets
  // # https://bigml.com/developers/datasets
  // #
  // ################################################################
  
  /**
   * Create a new dataset.
   *
   * POST /andromeda/dataset?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY; HTTP/1.1 Host:
   * bigml.io Content-Type: application/json
   *
   * @param sourceId	a unique identifier in the form source/id where id is a string of 24
   * alpha-numeric chars for the source to attach the dataset.
   * @param args	set of parameters for the new dataset. Optional
   * @param waitTime	time to wait for next check of FINISHED status for source before to start to
   * create the dataset. Optional
   *
   */
  public JSONObject createDataset(final String sourceId, String args, Integer waitTime) {
    return dataset.create(sourceId, args, waitTime);
  }

  
  /**
   * Retrieve a dataset.
   *
   * GET
   * /andromeda/dataset/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io
   *
   * @param datasetId a unique identifier in the form datset/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject getDataset(final String datasetId) {
    return dataset.get(datasetId);
  }

  
  /**
   * Retrieve a dataset.
   *
   * GET
   * /andromeda/dataset/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io
   *
   * @param dataset a unique identifier in the form datset/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject getDataset(final JSONObject datasetJSON) {
    return dataset.get(datasetJSON);
  }

  
  /**
   * Check whether a dataset' status is FINISHED.
   *
   * @param datasetId a unique identifier in the form dataset/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public boolean datasetIsReady(final String datasetId) {
    return dataset.isReady(datasetId);
  }

  
  /**
   * Check whether a dataset' status is FINISHED.
   *
   * @param dataset a unique identifier in the form dataset/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public boolean datasetIsReady(final JSONObject datasetJSON) {
    return dataset.isReady(datasetJSON);
  }

  
  /**
   * List all your datasources.
   *
   * GET /andromeda/dataset?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY; Host: bigml.io
   *
   * @param queryString	query filtering the listing.
   *
   */
  public JSONObject listDatasets(final String queryString) {
    return dataset.list(queryString);
  }

  
  /**
   * Update a dataset.
   *
   * PUT
   * /andromeda/dataset/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io Content-Type: application/json
   *
   * @param datasetId a unique identifier in the form dataset/id where id is a string of 24
   * alpha-numeric chars.
   * @param json	set of parameters to update the source. Optional
   *
   */
  public JSONObject updateDataset(final String datasetId, final String json) {
    return dataset.update(datasetId, json);
  }

  
  /**
   * Update a dataset.
   *
   * PUT
   * /andromeda/dataset/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io Content-Type: application/json
   *
   * @param dataset a unique identifier in the form dataset/id where id is a string of 24
   * alpha-numeric chars.
   * @param json	set of parameters to update the source. Optional
   *
   */
  public JSONObject updateDataset(final JSONObject datasetJSON, final JSONObject json) {
    return dataset.update(datasetJSON, json);
  }

  
  /**
   * Delete a dataset.
   *
   * DELETE
   * /andromeda/dataset/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1
   *
   * @param datasetId a unique identifier in the form dataset/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject deleteDataset(final String datasetId) {
    return dataset.delete(datasetId);
  }

  
  /**
   * Delete a dataset.
   *
   * DELETE
   * /andromeda/dataset/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1
   *
   * @param dataset a unique identifier in the form dataset/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject deleteDataset(final JSONObject datasetJSON) {
    return dataset.delete(datasetJSON);
  }

  
  // ################################################################
  // #
  // # Models
  // # https://bigml.com/developers/models
  // #
  // ################################################################
  
  /**
   * Create a new model.
   *
   * POST /andromeda/model?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY; HTTP/1.1 Host: bigml.io
   * Content-Type: application/json
   *
   * @param datsetId	a unique identifier in the form datset/id where id is a string of 24
   * alpha-numeric chars for the dataset to attach the model.
   * @param args	set of parameters for the new model. Optional
   * @param waitTime	time to wait for next check of FINISHED status for source before to start to
   * create the model. Optional
   *
   */
  public JSONObject createModel(final String datasetId, String args, Integer waitTime) {
    return model.create(datasetId, args, waitTime);
  }

  
  /**
   * Retrieve a model.
   *
   * GET /andromeda/model/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * Host: bigml.io
   *
   * @param modelId a unique identifier in the form model/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject getModel(final String modelId) {
    return model.get(modelId);
  }

  
  /**
   * Retrieve a model.
   *
   * GET /andromeda/model/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * Host: bigml.io
   *
   * @param model a unique identifier in the form model/id where id is a string of 24 alpha-numeric
   * chars.
   *
   */
  public JSONObject getModel(final JSONObject modelJSON) {
    return model.get(modelJSON);
  }

  
  /**
   * Check whether a model' status is FINISHED.
   *
   * @param modelId a unique identifier in the form model/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public boolean modelIsReady(final String modelId) {
    return model.isReady(modelId);
  }

  
  /**
   * Check whether a model' status is FINISHED.
   *
   * @param model a unique identifier in the form model/id where id is a string of 24 alpha-numeric
   * chars.
   *
   */
  public boolean modelIsReady(final JSONObject modelJSON) {
    return model.isReady(modelJSON);
  }

  
  /**
   * List all your models.
   *
   * GET /andromeda/model?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY; Host: bigml.io
   *
   * @param queryString	query filtering the listing.
   *
   */
  public JSONObject listModels(final String queryString) {
    return model.list(queryString);
  }

  
  /**
   * Update a model.
   *
   * PUT /andromeda/model/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io Content-Type: application/json
   *
   * @param modelId a unique identifier in the form model/id where id is a string of 24
   * alpha-numeric chars.
   * @param json	set of parameters to update the source. Optional
   *
   */
  public JSONObject updateModel(final String modelId, final String json) {
    return model.update(modelId, json);
  }

  
  /**
   * Update a model.
   *
   * PUT /andromeda/model/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io Content-Type: application/json
   *
   * @param model a unique identifier in the form model/id where id is a string of 24 alpha-numeric
   * chars.
   * @param json	set of parameters to update the source. Optional
   *
   */
  public JSONObject updateModel(final JSONObject modelJSON, final JSONObject json) {
    return model.update(modelJSON, json);
  }

  
  /**
   * Delete a model.
   *
   * DELETE
   * /andromeda/model/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1
   *
   * @param modelId a unique identifier in the form model/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject deleteModel(final String modelId) {
    return model.delete(modelId);
  }

  
  /**
   * Delete a model.
   *
   * DELETE
   * /andromeda/model/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1
   *
   * @param model unique identifier in the form model/id where id is a string of 24 alpha-numeric
   * chars.
   *
   */
  public JSONObject deleteModel(final JSONObject modelJSON) {
    return model.delete(modelJSON);
  }

  
  // ################################################################
  // #
  // # Predictions
  // # https://bigml.com/developers/predictions
  // #
  // ################################################################
  
  /**
   * Create a new prediction.
   *
   * POST /andromeda/prediction?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY; HTTP/1.1 Host:
   * bigml.io Content-Type: application/json
   *
   * @param modelId	a unique identifier in the form model/id where id is a string of 24
   * alpha-numeric chars for the source to attach the prediction.
   * @param args	set of parameters for the new prediction. Required
   * @param waitTime	time to wait for next check of FINISHED status for model before to start to
   * create the prediction. Optional
   *
   */
  public JSONObject createPrediction(final String modelId, String args, Integer waitTime) {
    return prediction.create(modelId, args, waitTime);
  }

  
  /**
   * Retrieve a prediction.
   *
   * GET
   * /andromeda/prediction/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io
   *
   * @param predictionId a unique identifier in the form prediction/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject getPrediction(final String predictionId) {
    return prediction.get(predictionId);
  }

  
  /**
   * Retrieve a prediction.
   *
   * GET
   * /andromeda/prediction/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io
   *
   * @param prediction a unique identifier in the form prediction/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public JSONObject getPrediction(final JSONObject predictionJSON) {
    return prediction.get(predictionJSON);
  }

  
  /**
   * Check whether a prediction' status is FINISHED.
   *
   * @param predictionId a unique identifier in the form prediction/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public boolean predictionIsReady(final String predictionId) {
    return prediction.isReady(predictionId);
  }

  
  /**
   * Check whether a prediction' status is FINISHED.
   *
   * @param prediction a unique identifier in the form prediction/id where id is a string of 24
   * alpha-numeric chars.
   *
   */
  public boolean predictionIsReady(final JSONObject predictionJSON) {
    return prediction.isReady(predictionJSON);
  }

  
  /**
   * List all your predictions.
   *
   * GET /andromeda/prediction?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY; Host: bigml.io
   *
   * @param queryString	query filtering the listing.
   *
   */
  public JSONObject listPredictions(final String queryString) {
    return prediction.list(queryString);
  }

  
  /**
   * Update a prediction.
   *
   * PUT /andromeda/model/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io Content-Type: application/json
   *
   * @param predictionId a unique identifier in the form prediction/id where id is a string of 24
   * alpha-numeric chars.
   * @param json	set of parameters to update the source. Optional
   *
   */
  public JSONObject updatePrediction(final String predictionId, final String json) {
    return prediction.update(predictionId, json);
  }

  
  /**
   * Update a prediction.
   *
   * PUT /andromeda/model/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io Content-Type: application/json
   *
   * @param prediction a unique identifier in the form prediction/id where id is a string of 24
   * alpha-numeric chars.
   * @param json	set of parameters to update the source. Optional
   *
   */
  public JSONObject updatePrediction(final JSONObject predictionJSON, final JSONObject json) {
    return prediction.update(predictionJSON, json);
  }

  
  /**
   * Delete a prediction.
   *
   * DELETE
   * /andromeda/prediction/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1
   *
   * @param predictionId a unique identifier in the form prediction/id where id is a string of 24
   * alpha-numeric chars
   *
   */
  public JSONObject deletePrediction(final String predictionId) {
    return prediction.delete(predictionId);
  }

  
  /**
   * Delete a prediction.
   *
   * DELETE
   * /andromeda/prediction/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1
   *
   * @param prediction a unique identifier in the form prediction/id where id is a string of 24
   * alpha-numeric chars
   *
   */
  public JSONObject deletePrediction(final JSONObject predictionJSON) {
    return prediction.delete(predictionJSON);
  }
  
  
  // ################################################################
  // #
  // # Evaluations
  // # https://bigml.com/developers/evaluations
  // #
  // ################################################################

  /**
   * Create a new evaluation.
   *
   * POST /andromeda/evaluation?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY; HTTP/1.1
   * Host: bigml.io
   * Content-Type: application/json
   *
   * @param modelId		a unique identifier in the form model/id where id is a string of 24
   *                	alpha-numeric chars for the model to attach the evaluation.
   * @param datasetId	a unique identifier in the form dataset/id where id is a string of 24
   * 					alpha-numeric chars for the dataset to attach the evaluation.
   * @param args		set of parameters for the new evaluation. Optional
   * @param waitTime	time to wait for next check of FINISHED status for model before to start to
   * 					create the evaluation. Optional
   *
   */
  public JSONObject createEvaluation(final String modelId, final String datasetId, String args, Integer waitTime) {
    return evaluation.create(modelId, datasetId, args, waitTime);
  }

 
  /**
   * Retrieve a evaluation.
   *
   * GET /andromeda/evaluation/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io
   *
   * @param evaluationId 	a unique identifier in the form evaluation/id where id 
   * 						is a string of 24 alpha-numeric chars.
   *
   */
  public JSONObject getEvaluation(final String evaluationId) {
    return evaluation.get(evaluationId);
  }

 
  /**
   * Retrieve a evaluation.
   *
   * GET /andromeda/evaluation/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io
   *
   * @param evaluationJSON 	a evaluation JSONObject.
   *
   */
  public JSONObject getEvaluation(final JSONObject evaluationJSON) {
    return evaluation.get(evaluationJSON);
  }

 
  /**
   * Check whether a evaluation' status is FINISHED.
   *
   * @param evaluationId 	a unique identifier in the form evaluation/id where id 
   * 						is a string of 24 alpha-numeric chars.
   *
   */
  public boolean evaluationIsReady(final String evaluationId) {
    return evaluation.isReady(evaluationId);
  }

 
  /**
   * Check whether a evaluation' status is FINISHED.
   *
   * @param evaluationJSON 	a evaluation JSONObject.
   *
   */
  public boolean evaluationIsReady(final JSONObject evaluationJSON) {
    return evaluation.isReady(evaluationJSON);
  }

 
  /**
   * List all your evaluations.
   *
   * GET /andromeda/evaluation?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY; Host: bigml.io
   *
   * @param queryString	query filtering the listing.
   *
   */
  public JSONObject listEvaluations(final String queryString) {
    return evaluation.list(queryString);
  }

 
  /**
   * Update a evaluation.
   *
   * PUT /andromeda/evaluation/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io Content-Type: application/json
   *
   * @param evaluationId	a unique identifier in the form evauation/id where id 
   * 						is a string of 24 alpha-numeric chars.
   * @param json			set of parameters to update the evaluation. Optional
   *
   */
  public JSONObject updateEvaluation(final String evaluationId, final String json) {
    return evaluation.update(evaluationId, json);
  }

 
  /**
   * Update a evaluation.
   *
   * PUT /andromeda/evaluation/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1 Host: bigml.io Content-Type: application/json
   *
   * @param evaluationJSON	a evaluation JSONObject
   * @param json			set of parameters to update the evaluation. Optional
   */
  public JSONObject updateEvaluation(final JSONObject evaluationJSON, final JSONObject json) {
    return evaluation.update(evaluationJSON, json);
  }

 
  /**
   * Delete a evaluation.
   *
   * DELETE /andromeda/evaluation/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1
   *
   * @param evaluationId 	a unique identifier in the form evaluation/id where id is a 
   * 						string of 24 alpha-numeric chars.
   *
   */
  public JSONObject deleteEvaluation(final String evaluationId) {
    return evaluation.delete(evaluationId);
  }

 
  /**
   * Delete a evaluation.
   *
   * DELETE /andromeda/evaluation/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
   * HTTP/1.1
   *
   * @param evaluationJSON 	a evaluation JSONObject.
   *
   */
  public JSONObject deleteEvaluation(final JSONObject evaluationJSON) {
    return evaluation.delete(evaluationJSON);
  }
 
}