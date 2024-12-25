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

resource "launchdarkly_feature_flag" "dashboard" {
  project_key = launchdarkly_project.demo.key
  key         = "dashboard"
  name        = "Dashboard"
  description = "Dashboard base flag"
  temporary   = false
  
  variation_type = "boolean"
  variations {
    value       = true
    name        = "True"
    description = "Dashboard is enabled"
  }
  variations {
    value       = false
    name        = "False"
    description = "Dashboard is disabled"
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

resource "launchdarkly_feature_flag" "dashboard_bar_chart" {
  project_key = launchdarkly_project.demo.key
  key         = "dashboard-bar-chart"
  name        = "Dashboard Bar Chart"
  description = "Dashboard bar chart component"
  temporary   = false
  
  variation_type = "boolean"
  variations {
    value       = true
    name        = "True"
    description = "Bar chart is visible"
  }
  variations {
    value       = false
    name        = "False"
    description = "Bar chart is hidden"
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

resource "launchdarkly_feature_flag" "dashboard_line_chart" {
  project_key = launchdarkly_project.demo.key
  key         = "dashboard-line-chart"
  name        = "Dashboard Line Chart"
  description = "Dashboard line chart component"
  temporary   = false
  
  variation_type = "boolean"
  variations {
    value       = true
    name        = "True"
    description = "Line chart is visible"
  }
  variations {
    value       = false
    name        = "False"
    description = "Line chart is hidden"
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

resource "launchdarkly_feature_flag" "dashboard_progress_meters" {
  project_key = launchdarkly_project.demo.key
  key         = "dashboard-progress-meters"
  name        = "Dashboard Progress Meters"
  description = "Dashboard progress meters component"
  temporary   = false
  
  variation_type = "boolean"
  variations {
    value       = true
    name        = "True"
    description = "Progress meters are visible"
  }
  variations {
    value       = false
    name        = "False"
    description = "Progress meters are hidden"
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
  triggers = {
    project_key = launchdarkly_project.demo.key
    flags = join(",", [
      launchdarkly_feature_flag.dashboard.key,
      launchdarkly_feature_flag.dashboard_bar_chart.key,
      launchdarkly_feature_flag.dashboard_line_chart.key,
      launchdarkly_feature_flag.dashboard_progress_meters.key
    ])
  }

  depends_on = [
    launchdarkly_feature_flag.dashboard,
    launchdarkly_feature_flag.dashboard_bar_chart,
    launchdarkly_feature_flag.dashboard_line_chart,
    launchdarkly_feature_flag.dashboard_progress_meters
  ]

  provisioner "local-exec" {
    command = <<EOT
    # Add prerequisites
    for flag in dashboard-bar-chart dashboard-line-chart dashboard-progress-meters; do
      for env in production test development; do
        echo "Setting prerequisite for $flag in $env environment..."
        curl -X PATCH \
          -H "Authorization: ${var.launchdarkly_access_token}" \
          -H "Content-Type: application/json" \
          "https://app.launchdarkly.com/api/v2/flags/${launchdarkly_project.demo.key}/$flag" \
          -d '{
            "patch": [{
              "op": "add",
              "path": "/environments/'$env'/prerequisites/-",
              "value": {
                "key": "dashboard",
                "variation": 0
              }
            }]
          }'
      done
    done
    EOT
  }
}