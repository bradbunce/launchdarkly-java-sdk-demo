terraform {
  required_providers {
    launchdarkly = {
      source = "launchdarkly/launchdarkly"
    }
  }
}

provider "launchdarkly" {
  access_token = var.launchdarkly_access_token
}

variable "user_name" {
  type = string
}

variable "launchdarkly_access_token" {
  type = string
}

resource "launchdarkly_project" "demo" {
  key  = "launchdarkly-java-demo-${lower(var.user_name)}"
  name = "LaunchDarkly Java Demo - ${var.user_name}"

  environments {
    key   = "production"
    name  = "Production"
    color = "417505"
  }

  environments {
    key   = "test"
    name  = "Test" 
    color = "FFFF00"
  }

  environments {
    key   = "development"
    name  = "Development"
    color = "FFA500"
  }
}

resource "launchdarkly_feature_flag" "form_1" {
  project_key = launchdarkly_project.demo.key
  key         = "form-1"
  name        = "Form 1"
  description = "Form 1 base flag"
  temporary   = false
  
  variation_type = "boolean"
  variations {
    value       = true
    name        = "True"
    description = "Flag is enabled"
  }
  variations {
    value       = false
    name        = "False"
    description = "Flag is disabled"
  }

  defaults {
    on_variation  = 0
    off_variation = 1
  }

  client_side_availability {
    using_environment_id = true
    using_mobile_key    = false
  }
}

resource "launchdarkly_feature_flag" "form_1_bar_chart" {
  project_key = launchdarkly_project.demo.key
  key         = "form-1-bar-chart"
  name        = "Form 1 Bar Chart"
  description = "Form 1 bar chart component"
  temporary   = false
  
  variation_type = "boolean"
  variations {
    value       = true
    name        = "True"
    description = "Chart is visible"
  }
  variations {
    value       = false
    name        = "False"
    description = "Chart is hidden"
  }

  defaults {
    on_variation  = 0
    off_variation = 1
  }

  client_side_availability {
    using_environment_id = true
    using_mobile_key    = false
  }
}

resource "launchdarkly_feature_flag" "form_1_line_chart" {
  project_key = launchdarkly_project.demo.key
  key         = "form-1-line-chart"
  name        = "Form 1 Line Chart"
  description = "Form 1 line chart component"
  temporary   = false
  
  variation_type = "boolean"
  variations {
    value       = true
    name        = "True"
    description = "Chart is visible"
  }
  variations {
    value       = false
    name        = "False"
    description = "Chart is hidden"
  }

  defaults {
    on_variation  = 0
    off_variation = 1
  }

  client_side_availability {
    using_environment_id = true
    using_mobile_key    = false
  }
}

resource "launchdarkly_feature_flag" "form_1_progress_meters" {
  project_key = launchdarkly_project.demo.key
  key         = "form-1-progress-meters"
  name        = "Form 1 Progress Meters"
  description = "Form 1 progress meters component"
  temporary   = false
  
  variation_type = "boolean"
  variations {
    value       = true
    name        = "True"
    description = "Meters are visible"
  }
  variations {
    value       = false
    name        = "False"
    description = "Meters are hidden"
  }

  defaults {
    on_variation  = 0
    off_variation = 1
  }

  client_side_availability {
    using_environment_id = true
    using_mobile_key    = false
  }
}

resource "null_resource" "add_prerequisites" {
  depends_on = [
    launchdarkly_feature_flag.form_1,
    launchdarkly_feature_flag.form_1_bar_chart,
    launchdarkly_feature_flag.form_1_line_chart,
    launchdarkly_feature_flag.form_1_progress_meters
  ]

  provisioner "local-exec" {
    command = <<EOT
    # Add prerequisites
    for flag in form-1-bar-chart form-1-line-chart form-1-progress-meters; do
      for env in production test development; do
        curl -X PATCH \
          -H "Authorization: ${var.launchdarkly_access_token}" \
          -H "Content-Type: application/json" \
          "https://app.launchdarkly.com/api/v2/flags/${launchdarkly_project.demo.key}/$flag" \
          -d '{
            "patch": [{
              "op": "add",
              "path": "/environments/'$env'/prerequisites/-",
              "value": {
                "key": "form-1",
                "variation": 0
              }
            }]
          }'
      done
    done
    EOT
  }
}