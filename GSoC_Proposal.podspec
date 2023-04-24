Pod::Spec.new do |spec|
    spec.name                     = 'GSoC_Proposal'
    spec.version                  = '0.0.1'
    spec.homepage                 = 'https://github.com/apple/swift-crypto'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'CacaoPods crypto lib'
    spec.vendored_frameworks      = 'build/cocoapods/framework/GSoC_Proposal.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '13.5'
    spec.dependency 'swift-crypto', '~> 2.5.0'
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => '',
        'PRODUCT_MODULE_NAME' => 'GSoC_Proposal',
    }
                
    spec.script_phases = [
        {
            :name => 'Build GSoC_Proposal',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../../../../../private/var/folders/tf/xcl_4zmx56jggyzlwqrqd9f00000gq/T/wrap3loc/gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end