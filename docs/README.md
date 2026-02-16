# Lab Strava

This repository contains a lab project for testing: 

- Claude code and spec-driven development
- SpringBoot integration with local LLM models
- Agentic development with a strong focus on code quality, well-defined architecture, and automated testing

### Functionality

The project aims to develop a SpringBoot app that:
- loads activities from Strava from their public API
- stores the activities in a local database
- provides a REST API for querying the activities
- stores a copy of activities in a vector database that can be used as RAG for question answering
- uses embeddings from the local vector database as an input to LLM
- provides API takes a query from user input, searches RAG and processes the query through LLM

### UX

- user logs-in
- user is asked to follow OAuth to authorize `lab-strava` app to connect to Strava API
- 

### Structure

This folder contains a documentation, iteration steps and plans for Claude Code on how to implement the project.

- `iterations`: human-defined specification broken down into smaller chunks of work
- `plans`: human-defined specification broken down into smaller chunks of work
